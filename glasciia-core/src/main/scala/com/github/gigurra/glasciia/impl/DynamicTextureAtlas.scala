package com.github.gigurra.glasciia.impl

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion
import com.badlogic.gdx.graphics.g2d.{TextureAtlas, TextureRegion}
import com.badlogic.gdx.graphics.glutils.GLOnlyTextureData
import com.badlogic.gdx.graphics.{GL20, Pixmap, Texture}
import com.github.gigurra.glasciia.Glasciia._
import com.github.gigurra.glasciia.Logging
import com.github.gigurra.glasciia.TextureRegionLoader.Conf
import com.github.gigurra.glasciia.impl.DynamicTextureAtlas.{Page, Strategy, SweepStrategy}
import com.github.gigurra.math.{Box2, Vec2}

import scala.collection.mutable
import scala.language.implicitConversions

/**
  * Created by johan on 2016-10-09.
  */
case class DynamicTextureAtlas(conf: Conf,
                               pageSize: Vec2 = Vec2(2048, 2048),
                               strategy: Strategy = SweepStrategy,
                               padding: Int = 10,
                               atlas: TextureAtlas = new TextureAtlas()) extends Logging {

  private val _pages = new mutable.ArrayBuffer[Page]
  private val regions = new mutable.HashMap[String, AtlasRegion]

  final def pages: Seq[Page] = {
    _pages
  }

  def get(name: String): Option[AtlasRegion] = {
    regions.get(name) match {
      case r @ Some(_) => r
      case None => Option(atlas.findRegion(name)) match {
        case None => None
        case r@Some(region) =>
          regions.put(name, region)
          r
      }
    }
  }

  def add(name: String,
          source: TextureRegion,
          flush: Boolean,
          deleteSource: Boolean): AtlasRegion = {

    val texture = source.getTexture
    val textureData = texture.getTextureData

    if (!textureData.isPrepared)
      textureData.prepare()

    val pixMap = texture.getTextureData.consumePixmap()
    val newRegion = add(name, pixMap, flush = flush, deleteSource = false)

    if (deleteSource) {
      pixMap.dispose()
      texture.dispose()
    }

    newRegion
  }

  def add(name: String,
          source: Pixmap,
          flush: Boolean,
          deleteSource: Boolean): AtlasRegion = {

    val (region, page) = reserve(name, source.width, source.height)
    page.blit(source, region)

    if (deleteSource)
      source.dispose()

    if (flush)
      page.flush(force = false)

    region
  }

  def remove(name: String): Unit = {
    for {
      region    <- regions.get(name)
      iPage     = pages.indexWhere(_.byName.contains(name))
      page      = pages(iPage)
    } {

      // Remove region
      regions.remove(name)
      page.remove(region)
      atlas.getRegions.removeValue(region, true)

      // Dispose the page if it's now empty
      if (page.isEmpty) {
        _pages.remove(iPage)
        atlas.getTextures.remove(page.texture)
        page.dispose()
      }
    }
  }

  def reserve(name: String,
              width: Int,
              height: Int): (AtlasRegion, DynamicTextureAtlas.Page) = {
    require(!regions.contains(name), s"Tried to reserve region with name $name twice!")
    require(width + padding * 2 <= pageSize.x, s"Tried to reserve region $name, but it was larger that the maximum allowed texture width - 2*padding, which set to ${pageSize.x - 2 * padding}")
    require(height + padding * 2 <= pageSize.y, s"Tried to reserve region $name, but it was larger that the maximum allowed texture height - 2*padding, which set to ${pageSize.y - 2 * padding}")

    val size = Vec2(width, height)

    object Fitting {
      def unapply(page: Page): Option[(Page, Vec2)] = {
        strategy.findPosition(size, padding = padding, page).map(pos => (page, pos))
      }
    }

    def addRegionToPage(page: Page, position: Vec2): (AtlasRegion, DynamicTextureAtlas.Page) = {
      val newRegion = page.reserve(name, to = position, width = width, height = height)
      regions.put(name, newRegion)
      atlas.getRegions.add(newRegion)
      (newRegion, page)
    }

    val out = pages.collectFirst {
      case Fitting(page, position) => (page, position)
    } match {
      case Some((page, position)) =>
        addRegionToPage(page, position)
      case None =>
        val glFormat = Pixmap.Format.toGlFormat(Pixmap.Format.RGBA8888)
        val glType = Pixmap.Format.toGlType(Pixmap.Format.RGBA8888)
        val newTexture = new Texture(new GLOnlyTextureData(pageSize.x.toInt, pageSize.y.toInt, 0, glFormat, glFormat, glType))
        newTexture.setFilter(conf.minFilter, conf.magFilter)
        atlas.getTextures.add(newTexture)
        val page = new Page(pageSize, newTexture)
        _pages += page
        addRegionToPage(page, position = Vec2.zero + padding)
    }

    log.debug(s"Placing $name at ${out._1.bounds}")

    out
  }

  def flush(force: Boolean): Unit = {
    pages.foreach(_.flush(force = force))
  }
}

object DynamicTextureAtlas {

  implicit def dynamic2atlas(dynamicTexturePackingAtlas: DynamicTextureAtlas): TextureAtlas = dynamicTexturePackingAtlas.atlas

  case class DirtyRegion(box2: Box2, needUpload: Boolean)
  object DirtyRegion {
    implicit def DR2b2(dr: DirtyRegion): Box2 = dr.box2
  }

  class Page(val capacity: Vec2,
             val texture: Texture,
             val byName: mutable.HashMap[String, AtlasRegion] = new mutable.HashMap[String, AtlasRegion],
             val bounds: mutable.ArrayBuffer[Box2] = new mutable.ArrayBuffer[Box2],
             var boundsSortedByTop: Vector[Box2] = Vector.empty) {

    private var mipmapsDirty = true

    def dispose(): Unit = {
      texture.dispose()
    }

    def remove(region: AtlasRegion): Unit = {
      byName.remove(region.name)
      bounds.remove(bounds.indexOf(region.bounds))
      boundsSortedByTop = bounds.sortBy(_.top).toVector
    }

    def isEmpty: Boolean = {
      byName.isEmpty
    }

    def blit(source: Pixmap, region: AtlasRegion): Unit = {

      // We pad up small images probably intended for filling,
      // since UV interpretations differ between OpenGL implementations
      val padUp: Boolean = region.width <= 5 && region.height <= 5

      val (pixmap, bounds, needDispose) =
        if (padUp) {

          val to: Vec2 = region.pos
          val marginX = math.max(0, to.x.toInt - 1)
          val marginY = math.max(0, to.y.toInt - 1)

          val marginBounds = Box2(
            x = marginX,
            y = marginY,
            width = math.min(source.width + 2, region.getTexture.width - marginX),
            height = math.min(source.height + 2, region.getTexture.height - marginY)
          )

          // UV coordinates are apparently interpreted differently depending on device... Better just draw the image again outside
          // to double up on the border
          val pixmapCopySettingBefore = Pixmap.getBlending
          Pixmap.setBlending(Pixmap.Blending.None)
          val tempBuffer = new Pixmap(marginBounds.width.toInt, marginBounds.height.toInt, source.getFormat)
          tempBuffer.drawPixmap(source, 0, 0, source.width, source.height, 0, 0, marginBounds.width.toInt, marginBounds.height.toInt)
          Pixmap.setBlending(pixmapCopySettingBefore)

          (tempBuffer, marginBounds, true)
        }
        else {
          (source, region.bounds, false)
        }

      texture.bind()
      Gdx.gl.glTexSubImage2D(
        GL20.GL_TEXTURE_2D,
        0,
        bounds.left.toInt,
        bounds.bottom.toInt,
        bounds.width.toInt,
        bounds.height.toInt,
        pixmap.getGLInternalFormat,
        pixmap.getGLType,
        pixmap.getPixels
      )

      if (needDispose) {
        pixmap.dispose()
      }

      mipmapsDirty = true
    }

    def dirty: Boolean = {
      mipmapsDirty
    }

    def flush(force: Boolean): Unit = {
      if (force || dirty) {
        texture.bind()
        Gdx.gl20.glGenerateMipmap(GL20.GL_TEXTURE_2D)
        mipmapsDirty = false
      }
    }

    def reserve(name: String, to: Vec2, width: Int, height: Int): AtlasRegion = {

      val region = new AtlasRegion(texture, to.x.toInt, to.y.toInt, width, height)

      val itemBounds = region.bounds
      region.name = name
      region.index = -1
      region.originalWidth = width
      region.originalHeight = height

      mipmapsDirty = true

      byName += name -> region
      bounds += itemBounds
      boundsSortedByTop = bounds.sortBy(_.top).toVector

      region
    }

  }

  trait Strategy {
    def findPosition(sourceSize: Vec2, padding: Int, page: Page): Option[Vec2]
  }

  object SweepStrategy extends Strategy {
    override def findPosition(sourceSize: Vec2, padding: Int, page: Page): Option[Vec2] = {

      if (page.bounds.isEmpty) {
        Some(Vec2.zero)
      } else {

        // This can prob be done with some magical functional solution, but I just dont care..
        // PLUS: The performance would likely suck since we're doing low level math here. Heck,
        // we should probably macro expand the for loop below

        val requiredSize = sourceSize + 2 * padding

        val yLim = page.capacity.y - requiredSize.y
        val xLim = page.capacity.x - requiredSize.x

        // Sort by top so we can mark some as guaranteed passed
        val bounds = page.boundsSortedByTop

        // Stupid scala when it comes to optimized loops. Give us back for-loops :S
        var passed = 0
        var yOffs = 0
        var answer: Option[Vec2] = None
        while (yOffs < yLim && answer.isEmpty) {
          var xOffs = 0
          while (xOffs < xLim && answer.isEmpty) {
            val left = xOffs
            val bottom = yOffs
            val right = left + requiredSize.x
            val top = bottom + requiredSize.y
            var collision = false
            var i = passed
            var collisionObstacleWidth = 0
            var obstacle: Box2 = null
            while (i < bounds.length && !collision) {
              obstacle = bounds(i)
              if (obstacle.notOverlaps(left, right, bottom, top)) {
                i += 1
              } else {
                collision = true
                collisionObstacleWidth = obstacle.width.toInt
              }
            }
            if (collision) {
              xOffs = obstacle.right.toInt + 1
            } else {
              answer = Some(Vec2(left, bottom) + padding)
            }
          }
          if (answer.isEmpty) {
            yOffs = bounds(passed).top.toInt + 1
            passed += 1
          }
        }

        answer
      }

    }
  }

}
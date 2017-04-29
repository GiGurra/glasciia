package com.github.gigurra.glasciia.impl

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.{SpriteBatcher, TextureRegion}
import com.badlogic.gdx.graphics.{Camera, GL20, Pixmap, Texture}
import com.github.gigurra.glasciia.Glasciia._
import com.github.gigurra.glasciia.{AtlasFrameBuffer, Logging, TextureConf}
import com.github.gigurra.glasciia.impl.DynamicTextureAtlas.{AtlasRegion, Page, Strategy, SweepStrategy}
import com.github.gigurra.math.{Box2, Vec2}

import scala.collection.mutable
import scala.language.implicitConversions

/**
  * Created by johan on 2016-10-09.
  */
case class DynamicTextureAtlas(textureConf: TextureConf = TextureConf(),
                               pageSize: Vec2 = Vec2(2048, 2048),
                               strategy: Strategy = SweepStrategy,
                               useFramebufferDepth: Boolean = false,
                               useFramebufferStencil: Boolean = false,
                               padding: Int = 8) extends Logging {

  require(padding % 4 == 0, s"Padding must be 4 byte aligned")

  private val _pages = new mutable.ArrayBuffer[Page]
  private val regions = new mutable.HashMap[String, AtlasRegion]

  final def pages: Seq[Page] = {
    _pages
  }

  def get(name: String): Option[AtlasRegion] = {
    regions.get(name)
  }

  def contains(name: String): Boolean = {
    regions.contains(name)
  }

  def add(name: String,
          source: Pixmap,
          deleteSource: Boolean,
          flush: Boolean): AtlasRegion = {

    require(source.getFormat == Pixmap.Format.RGBA8888, s"Can only add pixmaps to atlas of format RGBA8888, however '$name' is of format ${source.getFormat}")

    val region = reserve(name, source.width, source.height)
    val page = region.page

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

      // Dispose the page if it's now empty
      if (page.isEmpty) {
        _pages.remove(iPage)
        page.dispose()
      }
    }
  }

  def paint(region: AtlasRegion,
            batch: SpriteBatcher,
            projection: Camera,
            clear: Boolean = true)(content: => Unit): Unit = {
    region.page.paint(
      region.bounds,
      batch,
      projection,
      clear
    )(content)
  }

  def reserve(name: String,
              width: Int,
              height: Int): AtlasRegion = {
    require(!regions.contains(name), s"Tried to reserve region with name '$name' twice!")
    require(width + padding * 2 <= pageSize.x, s"Tried to reserve region '$name', but it was larger that the maximum allowed texture width - 2*padding, which set to ${pageSize.x - 2 * padding}")
    require(height + padding * 2 <= pageSize.y, s"Tried to reserve region '$name', but it was larger that the maximum allowed texture height - 2*padding, which set to ${pageSize.y - 2 * padding}")

    val size = Vec2(width, height)

    object Fitting {
      def unapply(page: Page): Option[(Page, Vec2)] = {
        strategy.findPosition(size, padding = padding, page).map(pos => (page, pos))
      }
    }

    def addRegionToPage(page: Page, position: Vec2): AtlasRegion = {
      val newRegion = page.reserve(name, to = position, width = width, height = height)
      regions.put(name, newRegion)
      newRegion
    }

    val out = pages.collectFirst {
      case Fitting(page, position) =>
        require(is4ByteAligned(position), s"Atlas strategy returned non 4-byte aligned atlas position for: '$name', pos: $position")
        (page, position)
    } match {
      case Some((page, position)) =>
        addRegionToPage(page, position)
      case None =>
        val frameBuffer = new AtlasFrameBuffer(
          width = pageSize.x.toInt,
          height = pageSize.y.toInt,
          format = Pixmap.Format.RGBA8888,
          useDepth = useFramebufferDepth,
          useStencil = useFramebufferStencil,
          textureConf = textureConf
        )
        val page = new Page(pageSize, frameBuffer)
        _pages += page
        addRegionToPage(page, position = Vec2.zero + padding)
    }

    log.debug(s"Placing $name at ${out.bounds}")

    out
  }

  def flush(force: Boolean): Unit = {
    pages.foreach(_.flush(force = force))
  }

  def clear(): Unit = {
    pages.foreach(_.dispose())
    _pages.clear()
    regions.clear()
  }

  private def is4ByteAligned(position: Vec2): Boolean = {
    position.x % 4 == 0 && position.y % 4 == 0
  }
}

object DynamicTextureAtlas {

  case class DirtyRegion(box2: Box2, needUpload: Boolean)
  object DirtyRegion {
    implicit def DR2b2(dr: DirtyRegion): Box2 = dr.box2
  }

  case class AtlasRegion(name: String, page: Page, bounds: Box2)
    extends TextureRegion(page.texture, bounds.left.toInt, bounds.bottom.toInt, bounds.width.toInt, bounds.height.toInt) {
    def pos: Vec2 = bounds.ll
    def paint(batch: SpriteBatcher,
              projection: Camera,
              clear: Boolean = true)(content: => Unit): Unit = {
      page.paint(bounds, batch, projection, clear)(content)
    }
  }
  object AtlasRegion {
    implicit def ar2r(ar: AtlasRegion): Box2 = ar.bounds
  }

  class Page(val capacity: Vec2,
             val frameBuffer: AtlasFrameBuffer,
             val byName: mutable.HashMap[String, AtlasRegion] = new mutable.HashMap[String, AtlasRegion],
             val bounds: mutable.ArrayBuffer[Box2] = new mutable.ArrayBuffer[Box2],
             var boundsSortedByTop: Vector[Box2] = Vector.empty) {

    private var mipmapsDirty = true

    def texture: Texture = {
      frameBuffer.texture
    }

    def textureConf: TextureConf = {
      frameBuffer.textureConf
    }

    def dispose(): Unit = {
      frameBuffer.dispose()
    }

    def remove(region: AtlasRegion): Unit = {
      byName.remove(region.name)
      bounds.remove(bounds.indexOf(region.bounds))
      boundsSortedByTop = bounds.sortBy(_.top).toVector
    }

    def isEmpty: Boolean = {
      byName.isEmpty
    }

    def blit(source: Pixmap, region: Box2): Unit = {

      // We pad up small images probably intended for filling,
      // since UV interpretations differ between OpenGL implementations
      val padUp: Boolean = region.width <= 5 && region.height <= 5

      val (pixMap, bounds, needDispose) =
        if (padUp) {

          val to: Vec2 = region.ll
          val marginX = math.max(0, to.x.toInt - 1)
          val marginY = math.max(0, to.y.toInt - 1)

          val marginBounds = Box2(
            x = marginX,
            y = marginY,
            width = math.min(source.width + 2, texture.width - marginX),
            height = math.min(source.height + 2, texture.height - marginY)
          )

          // UV coordinates are apparently interpreted differently depending on device... Better just draw the image again outside
          // to double up on the border
          val tempBuffer = new Pixmap(marginBounds.width.toInt, marginBounds.height.toInt, source.getFormat)
          val pixMapCopySettingBefore = tempBuffer.getBlending
          tempBuffer.setBlending(Pixmap.Blending.None)
          tempBuffer.drawPixmap(source, 0, 0, source.width, source.height, 0, 0, marginBounds.width.toInt, marginBounds.height.toInt)
          tempBuffer.setBlending(pixMapCopySettingBefore)

          (tempBuffer, marginBounds, true)
        }
        else {
          (source, region, false)
        }

      texture.bind()
      Gdx.gl.glTexSubImage2D(
        GL20.GL_TEXTURE_2D,
        0,
        bounds.left.toInt,
        bounds.bottom.toInt,
        bounds.width.toInt,
        bounds.height.toInt,
        pixMap.getGLInternalFormat,
        pixMap.getGLType,
        pixMap.getPixels
      )

      if (needDispose) {
        pixMap.dispose()
      }

      mipmapsDirty = true
    }

    def paint(region: Box2,
              batch: SpriteBatcher,
              projection: Camera,
              clear: Boolean = true)(content: => Unit): Unit = {
      frameBuffer.use(
        region = region,
        batch = batch,
        projection = projection,
        clear = clear
      )(content)
    }

    def dirty: Boolean = {
      mipmapsDirty
    }

    def flush(force: Boolean): Unit = {
      if (force || dirty) {
        if (textureConf.useMipMaps) {
          texture.bind()
          Gdx.gl20.glGenerateMipmap(GL20.GL_TEXTURE_2D)
        }
        mipmapsDirty = false
      }
    }

    def reserve(name: String, to: Vec2, width: Int, height: Int): AtlasRegion = {

      val region = AtlasRegion(name, this, Box2(to.x, to.y, width, height))
      val itemBounds = region.bounds

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
              xOffs += (4 - xOffs % 4) // 4 byte alignment
            } else {
              answer = Some(Vec2(left, bottom) + padding)
            }
          }
          if (answer.isEmpty) {
            yOffs = bounds(passed).top.toInt + 1
            yOffs += (4 - yOffs % 4) // 4 byte alignment
            passed += 1
          }
        }

        answer
      }

    }
  }

}
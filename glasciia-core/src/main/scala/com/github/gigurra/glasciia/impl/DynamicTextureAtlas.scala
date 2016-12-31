package com.github.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion
import com.badlogic.gdx.graphics.g2d.{TextureAtlas, TextureRegion}
import com.badlogic.gdx.graphics.glutils.PixmapTextureData
import com.badlogic.gdx.graphics.{Pixmap, Texture}
import com.github.gigurra.glasciia.Glasciia._
import com.github.gigurra.glasciia.Logging
import com.github.gigurra.glasciia.TextureRegionLoader.Conf
import com.github.gigurra.glasciia.impl.DynamicTextureAtlas.{Page, Strategy, SweepStrategy}
import com.github.gigurra.math.{Box2, Vec2}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.language.implicitConversions

/**
  * Created by johan on 2016-10-09.
  */
case class DynamicTextureAtlas(conf: Conf,
                               pageSize: Vec2 = Vec2(2048, 2048),
                               strategy: Strategy = SweepStrategy,
                               padding: Int = 10,
                               atlas: TextureAtlas = new TextureAtlas()) extends Logging {

  private val pages = new ArrayBuffer[Page]()
  private val lookup = new mutable.HashMap[String, AtlasRegion]()

  def get(name: String): Option[AtlasRegion] = {
    lookup.get(name) match {
      case r @ Some(_) => r
      case None => Option(atlas.findRegion(name)) match {
        case None => None
        case r@Some(region) =>
          lookup.put(name, region)
          r
      }
    }
  }

  def add(name: String,
          source: TextureRegion,
          upload: Boolean,
          deleteSource: Boolean): AtlasRegion = {
    val texture = source.getTexture
    val textureData = texture.getTextureData
    if (!textureData.isPrepared)
      textureData.prepare()
    val pixMap = texture.getTextureData.consumePixmap()
    val newRegion = add(name, pixMap, upload = upload, deleteSource = false)
    if (deleteSource) {
      pixMap.dispose()
      texture.dispose()
    }
    newRegion
  }

  def add(name: String,
          source: Pixmap,
          upload: Boolean,
          deleteSource: Boolean): AtlasRegion = {
    require(source.getWidth + padding <= pageSize.x, s"Tried to add region $name, but it was larger that the maximum allowed texture width - 2*padding, which set to ${pageSize.x - 2 * padding}")
    require(source.getHeight + padding <= pageSize.y, s"Tried to add region $name, but it was larger that the maximum allowed texture height - 2*padding, which set to ${pageSize.y - 2 * padding}")

    object Fitting {
      def unapply(page: Page): Option[(Page, Vec2)] = {
        strategy.findPosition(source.size, padding = padding, page).map(pos => (page, pos))
      }
    }

    def addRegionToPage(page: Page, position: Vec2, upload: Boolean): AtlasRegion = {
      val newRegion = page.copyIn(name, source, position, upload = upload)
      lookup.put(name, newRegion)
      atlas.getRegions.add(newRegion)
      newRegion
    }

    val out = pages.collectFirst {
      case Fitting(page, position) => (page, position)
    } match {
      case Some((page, position)) =>
        addRegionToPage(page, position, upload = upload)
      case None =>
        val newTexture = new Texture(new PixmapTextureData(new Pixmap(pageSize.x.toInt, pageSize.y.toInt, Pixmap.Format.RGBA8888), null: Pixmap.Format, conf.useMipMaps, false))
        newTexture.setFilter(conf.minFilter, conf.magFilter)
        atlas.getTextures.add(newTexture)
        val page = new Page(pageSize, newTexture)
        pages += page
        addRegionToPage(page, position = Vec2.zero + padding, upload = upload)
    }

    if (deleteSource)
      source.dispose()

    log.debug(s"Placing $name at ${out.bounds}")

    out
  }

  def uploadIfDirty(): Unit = {
    pages.foreach(_.uploadIfDirty())
  }
}

object DynamicTextureAtlas {

  implicit def dynamic2atlas(dynamicTexturePackingAtlas: DynamicTextureAtlas): TextureAtlas = dynamicTexturePackingAtlas.atlas

  class Page(val capacity: Vec2,
             val texture: Texture,
             val byName: mutable.HashMap[String, AtlasRegion] = new mutable.HashMap[String, AtlasRegion],
             val bounds: mutable.ArrayBuffer[Box2] = new mutable.ArrayBuffer[Box2],
             var boundsSortedByTop: Vector[Box2] = Vector.empty,
             var dirty: Boolean = false) {

    private val singlePixelUV = 1.0f / texture.width.toFloat
    private val halfPixelUV = singlePixelUV / 2.0f
    private val quarterPixelUV = halfPixelUV / 2.0f

    def copyIn(name: String, source: Pixmap, to: Vec2, upload: Boolean): AtlasRegion = {
      val region = new AtlasRegion(texture, to.x.toInt, to.y.toInt, source.width, source.height)

      // Override libgdx crappy hacks. Gdx sets the UV values to the outer edges of the pixels
      // But we want the UV values to always hit center pixels.
      // Gdx also has a special hack for 1x1 textures which, for some reason,
      // sets it to 0.25 into the pixel >P.
      // If we call set region again with the same value for a 1x1-region, it will be
      // fixed and set to center pixel. For other values, half a pixel offset works
      if (source.width == 1 && source.height == 1) {
        region.setRegion(
          region.getU,
          region.getV,
          region.getU2,
          region.getV2
        )
      } else {
        region.setRegion(
          region.getU + quarterPixelUV,
          region.getV + quarterPixelUV,
          region.getU2 - quarterPixelUV,
          region.getV2 - quarterPixelUV
        )
      }

      val itemBounds = region.bounds
      region.name = name
      region.index = -1
      region.originalWidth = source.getWidth
      region.originalHeight = source.getHeight

      val targetPixMap = texture.getTextureData.consumePixmap()
      val pixmapCopySettingBefore = Pixmap.getBlending
      try {
        Pixmap.setBlending(Pixmap.Blending.None)
        targetPixMap.drawPixmap(source, to.x.toInt, to.y.toInt)
      } finally {
        Pixmap.setBlending(pixmapCopySettingBefore)
      }


      dirty = true

      if (upload)
        uploadIfDirty()

      byName += name -> region
      bounds += itemBounds
      boundsSortedByTop = bounds.sortBy(_.top).toVector

      region
    }

    def uploadIfDirty(): Unit = {
      if (dirty) {
        texture.load(texture.getTextureData)
        dirty = false
      }
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
package com.github.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion
import com.badlogic.gdx.graphics.g2d.{TextureAtlas, TextureRegion}
import com.badlogic.gdx.graphics.glutils.PixmapTextureData
import com.badlogic.gdx.graphics.{Pixmap, Texture}
import com.github.gigurra.glasciia.Glasciia._
import com.github.gigurra.glasciia.TextureRegionLoader.Conf
import com.github.gigurra.glasciia.impl.DynamicTextureAtlas.{Page, Strategy, SweepStrategy}
import com.github.gigurra.math.{Box2, Vec2, Zero}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.language.implicitConversions

/**
  * Created by johan on 2016-10-09.
  */
case class DynamicTextureAtlas(conf: Conf,
                               pageSize: Vec2[Int] = Vec2[Int](2048, 2048),
                               strategy: Strategy = SweepStrategy,
                               padding: Int = 10,
                               atlas: TextureAtlas = new TextureAtlas()) {

  private val pages = new ArrayBuffer[Page]()
  private val lookup = new mutable.HashMap[String, AtlasRegion]()

  def get(name: String): Option[AtlasRegion] = {
    lookup.get(name) match {
      case r@Some(region) => r
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
      def unapply(page: Page): Option[(Page, Vec2[Int])] = {
        strategy.findPosition(source.size, padding = padding, page).map(pos => (page, pos))
      }
    }

    def addRegionToPage(page: Page, position: Vec2[Int], upload: Boolean): AtlasRegion = {
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
        val newTexture = new Texture(new PixmapTextureData(new Pixmap(pageSize.x, pageSize.y, Pixmap.Format.RGBA8888), null: Pixmap.Format, conf.useMipMaps, false))
        newTexture.setFilter(conf.minFilter, conf.magFilter)
        atlas.getTextures.add(newTexture)
        val page = new Page(pageSize, newTexture)
        pages += page
        addRegionToPage(page, position = Zero.vec2i + padding, upload = upload)
    }

    if (deleteSource)
      source.dispose()

    println(s"Placing $name at ${out.bounds}")

    out
  }

  def uploadIfDirty(): Unit = {
    pages.foreach(_.uploadIfDirty())
  }
}

object DynamicTextureAtlas {

  implicit def dynamic2atlas(dynamicTexturePackingAtlas: DynamicTextureAtlas): TextureAtlas = dynamicTexturePackingAtlas.atlas

  class Page(val capacity: Vec2[Int],
             val texture: Texture,
             val byName: mutable.HashMap[String, AtlasRegion] = new mutable.HashMap[String, AtlasRegion],
             val bounds: mutable.ArrayBuffer[Box2[Int]] = new mutable.ArrayBuffer[Box2[Int]],
             var boundsSortedByTop: Seq[Box2[Int]] = Nil,
             var dirty: Boolean = false) {

    def copyIn(name: String, source: Pixmap, to: Vec2[Int], upload: Boolean): AtlasRegion = {
      val region = new AtlasRegion(texture, to.x, to.y, source.width, source.height)
      val itemBounds = region.bounds
      region.name = name
      region.index = -1
      region.originalWidth = source.getWidth
      region.originalHeight = source.getHeight

      val targetPixMap = texture.getTextureData.consumePixmap()
      targetPixMap.drawPixmap(source, to.x, to.y)

      dirty = true

      if (upload)
        uploadIfDirty()

      byName += name -> region
      bounds += itemBounds
      boundsSortedByTop = bounds.sortBy(_.top)

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
    def findPosition(sourceSize: Vec2[Int], padding: Int, page: Page): Option[Vec2[Int]]
  }

  object SweepStrategy extends Strategy {
    override def findPosition(sourceSize: Vec2[Int], padding: Int, page: Page): Option[Vec2[Int]] = {

      if (page.bounds.isEmpty) {
        Some(Zero.vec2i)
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
        var answer: Option[Vec2[Int]] = None
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
            var obstacle: Box2[Int] = null
            while (i < bounds.length && !collision) {
              obstacle = bounds(i)
              if (obstacle.notOverlaps(left, right, bottom, top)) {
                i += 1
              } else {
                collision = true
                collisionObstacleWidth = obstacle.width
              }
            }
            if (collision) {
              xOffs = obstacle.right + 1
            } else {
              answer = Some(Vec2(left, bottom) + padding)
            }
          }
          if (answer.isEmpty) {
            yOffs = bounds(passed).top + 1
            passed += 1
          }
        }

        answer
      }

    }
  }

}
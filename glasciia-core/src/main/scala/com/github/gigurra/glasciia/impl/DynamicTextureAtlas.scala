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
import scala.language.implicitConversions

/**
  * Created by johan on 2016-10-09.
  */
case class DynamicTextureAtlas(conf: Conf,
                               pageSize: Vec2 = Vec2(2048, 2048),
                               strategy: Strategy = SweepStrategy,
                               padding: Int = 10,
                               atlas: TextureAtlas = new TextureAtlas()) extends Logging {

  private val pages = new mutable.ArrayBuffer[Page]
  private val regions = new mutable.HashMap[String, AtlasRegion]

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
          rebuildMipmaps: Boolean,
          deleteSource: Boolean): AtlasRegion = {

    val texture = source.getTexture
    val textureData = texture.getTextureData

    if (!textureData.isPrepared)
      textureData.prepare()

    val pixMap = texture.getTextureData.consumePixmap()
    val newRegion = add(name, pixMap, rebuildMipmaps = rebuildMipmaps, deleteSource = false)

    if (deleteSource) {
      pixMap.dispose()
      texture.dispose()
    }

    newRegion
  }

  def add(name: String,
          source: Pixmap,
          rebuildMipmaps: Boolean,
          deleteSource: Boolean): AtlasRegion = {

    val (region, page) = reserve(name, source.width, source.height)
    page.blit(source, region, rebuildMipmaps = rebuildMipmaps)

    if (deleteSource)
      source.dispose()

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
        pages.remove(iPage)
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
        val newTexture = new Texture(new PixmapTextureData(new Pixmap(pageSize.x.toInt, pageSize.y.toInt, Pixmap.Format.RGBA8888), null: Pixmap.Format, conf.useMipMaps, false))
        newTexture.setFilter(conf.minFilter, conf.magFilter)
        atlas.getTextures.add(newTexture)
        val page = new Page(pageSize, newTexture)
        pages += page
        addRegionToPage(page, position = Vec2.zero + padding)
    }

    log.debug(s"Placing $name at ${out._1.bounds}")

    out
  }

  def rebuildMipmaps(force: Boolean): Unit = {
    pages.foreach(_.buildMipmaps(force = force))
  }
}

object DynamicTextureAtlas {

  implicit def dynamic2atlas(dynamicTexturePackingAtlas: DynamicTextureAtlas): TextureAtlas = dynamicTexturePackingAtlas.atlas

  class Page(val capacity: Vec2,
             val texture: Texture,
             val byName: mutable.HashMap[String, AtlasRegion] = new mutable.HashMap[String, AtlasRegion],
             val bounds: mutable.ArrayBuffer[Box2] = new mutable.ArrayBuffer[Box2],
             var boundsSortedByTop: Vector[Box2] = Vector.empty,
             var mipmapsDirty: Boolean = false) {

    def dispose(): Unit = {
      texture.dispose()
    }

    def remove(region: AtlasRegion): Unit = {
      byName.remove(region.name)
      bounds.remove(bounds.indexOf(region.bounds))
      boundsSortedByTop = bounds.sortBy(_.top).toVector
      mipmapsDirty = true
    }

    def isEmpty: Boolean = {
      byName.isEmpty
    }

    def blit(source: Pixmap, region: AtlasRegion, rebuildMipmaps: Boolean): Unit = {

      val to: Vec2 = region.pos
      val targetPixMap = texture.getTextureData.consumePixmap()
      val pixmapCopySettingBefore = Pixmap.getBlending
      try {
        Pixmap.setBlending(Pixmap.Blending.None)
        // UV coordinates are apparently interpreted differently depending on device... Better just draw the image again outside
        // to double up on the border
        targetPixMap.drawPixmap(source, 0, 0, source.width, source.height, math.max(0, to.x.toInt-1), math.max(0, to.y.toInt-1), source.width+2, source.height+2)
        targetPixMap.drawPixmap(source, to.x.toInt, to.y.toInt)
      } finally {
        Pixmap.setBlending(pixmapCopySettingBefore)
      }

      mipmapsDirty = true

      if (rebuildMipmaps)
        buildMipmaps(force = false)
    }

    def buildMipmaps(force: Boolean): Unit = {
      if (force || mipmapsDirty) {
        texture.load(texture.getTextureData)
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

      mipmapsDirty = true // Assume you will fill this region in externally

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
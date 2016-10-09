package se.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.{Pixmap, Texture}
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion
import com.badlogic.gdx.graphics.g2d.{TextureAtlas, TextureRegion}
import com.badlogic.gdx.graphics.glutils.PixmapTextureData
import se.gigurra.glasciia.TextureRegionLoader.Conf
import se.gigurra.glasciia.impl.DynamicTexturePackingAtlas.{Page, Strategy, SweepStrategy}
import se.gigurra.math.{Box2, Vec2}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.language.implicitConversions

/**
  * Created by johan on 2016-10-09.
  */
case class DynamicTexturePackingAtlas(conf: Conf,
                                      pageSize: Vec2[Int] = Vec2[Int](2048, 2048),
                                      strategy: Strategy = SweepStrategy(),
                                      padding: Int = 10,
                                      atlas: TextureAtlas = new TextureAtlas()) {

  private val pages = new ArrayBuffer[Page]()

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

    val paddedSize = Vec2(source.getWidth, source.getHeight) + 2 * padding

    object Fitting {
      def unapply(page: Page): Option[(Page, Vec2[Int])] = {
        strategy.findPosition(paddedSize, page).map(pos => (page, pos))
      }
    }

    def addRegionToPage(page: Page, position: Vec2[Int], upload: Boolean): AtlasRegion = {
      val newRegion = page.copyIn(name, source, position, upload = upload)
      atlas.getRegions.add(newRegion)
      newRegion
    }

    val out = pages.collectFirst {
      case Fitting(page, position) => (page, position)
    } match {
      case Some((page, position)) =>
        addRegionToPage(page, position, upload = upload)
      case None =>
        val newTexture = new Texture(new PixmapTextureData(new Pixmap(pageSize.x, pageSize.y, Pixmap.Format.RGBA8888), null : Pixmap.Format, conf.useMipMaps, false))
        newTexture.setFilter(conf.minFilter, conf.magFilter)
        atlas.getTextures.add(newTexture)
        val page = new Page(pageSize, newTexture)
        val position = Vec2(0,0)
        pages += page
        addRegionToPage(page, position, upload = upload)
    }

    if (deleteSource)
      source.dispose()

    out
  }

  def uploadIfDirty(): Unit = {
    pages.foreach(_.uploadIfDirty())
  }
}

object DynamicTexturePackingAtlas {

  implicit def dynamic2atlas(dynamicTexturePackingAtlas: DynamicTexturePackingAtlas): TextureAtlas = dynamicTexturePackingAtlas.atlas

  class Page(val capacity: Vec2[Int],
             val texture: Texture,
             val byName: mutable.HashMap[String, AtlasRegion] = new mutable.HashMap[String, AtlasRegion],
             val bounds: mutable.ArrayBuffer[Box2[Int]] = new mutable.ArrayBuffer[Box2[Int]],
             var dirty: Boolean = false) {

    def copyIn(name: String, source: Pixmap, to: Vec2[Int], upload: Boolean): AtlasRegion = {
      val region = new AtlasRegion(texture, to.x, to.y, source.getWidth, source.getHeight)
      val itemBounds = Box2(ll = to, size = Vec2(source.getWidth, source.getHeight))
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

      println(s"Loaded $name into $this at $itemBounds")

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
    def findPosition(requiredSize: Vec2[Int], page: Page): Option[Vec2[Int]]
  }

  case class SweepStrategy(minStep: Int = 10) extends Strategy {
    override def findPosition(requiredSize: Vec2[Int], page: Page): Option[Vec2[Int]] = {

      // This can prob be done with some magical functional solution, but I just dont care..
      // PLUS: The performance would likely suck since we're doing low level math here. Heck,
      // we should probably macro expand the for loop below

      val yLim = page.capacity.y - requiredSize.y
      val xLim = page.capacity.x - requiredSize.x

      for {
        yOffs <- 0 until yLim //by requiredSize.x
        xOffs <- 0 until xLim //by requiredSize.y
        bounds = Box2[Int](ll = Vec2[Int](xOffs, yOffs), size = requiredSize)
        if page.bounds.forall(_.notOverlaps(bounds))
      } {
        return Some(bounds.ll)
      }

      None
    }
  }

}
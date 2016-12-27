package com.github.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.g2d.{PolygonRegion, TextureRegion}
import com.badlogic.gdx.graphics.{Pixmap, Texture}
import com.github.gigurra.math.{Box2, Vec2}

import scala.language.implicitConversions

/**
  * Created by johan on 2016-10-03.
  */
trait ImageImplicits {
  import ImageImplicitsImpl._

  implicit def img2TextureSizeImplicits(texture: Texture): TextureSizeImplicits = {
    new TextureSizeImplicits(texture)
  }

  implicit def img2PixMapSizeImplicits(pixmap: Pixmap): PixMapSizeImplicits = {
    new PixMapSizeImplicits(pixmap)
  }

  implicit def img2TextureRegionSizeImplicits(region: TextureRegion): TextureRegionSizeImplicits = {
    new TextureRegionSizeImplicits(region)
  }

  implicit def img2PolygonRegionSizeImplicits(polygon: PolygonRegion): PolygonRegionSizeImplicits = {
    new PolygonRegionSizeImplicits(polygon)
  }
}

object ImageImplicits extends ImageImplicits

object ImageImplicitsImpl {

  implicit class TextureSizeImplicits(val texture: Texture) extends AnyVal {
    def size: Vec2 = Vec2(width, height)
    def width: Int = texture.getWidth
    def height: Int = texture.getHeight
    def u: Float = 0.0f
    def u2: Float = 1.0f
    def v: Float = 0.0f
    def v2: Float = 1.0f
    def uuSize: Float = u2 - u
    def vvSize: Float = v2 - v

    def slice(x: Int, y: Int, width: Int, height: Int): TextureRegion = new TextureRegion(texture, x, y, width, height)
    def sliceUV(u: Float, v: Float, u2: Float, v2: Float): TextureRegion = new TextureRegion(texture, u, v, u2, v2)
    def sliceFraction(x: Float, y: Float, width: Float, height: Float): TextureRegion = {
      val uAbs = u + x * uuSize
      val vAbs = v + y * vvSize
      new TextureRegion(texture, uAbs, vAbs, uAbs + width * uuSize, vAbs + height * vvSize)
    }
  }

  implicit class PixMapSizeImplicits(val pixmap: Pixmap) extends AnyVal {
    def size: Vec2 = Vec2(width, height)
    def width: Int = pixmap.getWidth
    def height: Int = pixmap.getHeight
    def u: Float = 0.0f
    def u2: Float = 1.0f
    def v: Float = 0.0f
    def v2: Float = 1.0f
    def uuSize: Float = u2 - u
    def vvSize: Float = v2 - v
  }

  implicit class TextureRegionSizeImplicits(val region: TextureRegion) extends AnyVal {
    def size: Vec2 = Vec2(width, height)
    def width: Int = region.getRegionWidth
    def height: Int = region.getRegionHeight
    def x: Int = region.getRegionX
    def y: Int = region.getRegionY
    def pos: Vec2 = Vec2(x, y)
    def bounds: Box2 = Box2(ll = pos, size = size)
    def u: Float = region.getU
    def u2: Float = region.getU2
    def v: Float = region.getV
    def v2: Float = region.getV2
    def uuSize: Float = u2 - u
    def vvSize: Float = v2 - v

    def slice(x: Int, y: Int, width: Int, height: Int): TextureRegion = new TextureRegion(region, x, y, width, height)
    def sliceUV(u: Float, v: Float, u2: Float, v2: Float): TextureRegion = new TextureRegion(region.getTexture, u, v, u2, v2)
    def sliceFraction(x: Float, y: Float, width: Float, height: Float): TextureRegion = {
      val uAbs = u + x * uuSize
      val vAbs = v + y * vvSize
      new TextureRegion(region.getTexture, uAbs, vAbs, uAbs + width * uuSize, vAbs + height * vvSize)
    }
  }

  implicit class PolygonRegionSizeImplicits(val polygon: PolygonRegion) extends AnyVal {
    def region: TextureRegion = polygon.getRegion
    def regionSize: Vec2 = Vec2(regionWidth, regionHeight)
    def regionWidth: Int = region.getRegionWidth
    def regionHeight: Int = region.getRegionHeight
    def regionX: Int = region.getRegionX
    def regionY: Int = region.getRegionY
    def regionPos: Vec2 = Vec2(regionX, regionY)
    def regionBounds: Box2 = Box2(ll = regionPos, size = regionSize)
    def regionU: Float = region.getU
    def regionU2: Float = region.getU2
    def regionV: Float = region.getV
    def regionV2: Float = region.getV2
    def regionUUSize: Float = regionU2 - regionU
    def regionVVSize: Float = regionV2 - regionV
  }
}

package se.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.{Pixmap, Texture}
import se.gigurra.math.{Box2, Vec2}

import scala.language.implicitConversions

/**
  * Created by johan on 2016-10-03.
  */
trait ImageImplicits {

  implicit class TextureSizeImplicits(tex: Texture) {
    def size: Vec2[Int] = Vec2(width, height)
    def width: Int = tex.getWidth
    def height: Int = tex.getHeight
  }

  implicit class PixMapSizeImplicits(tex: Pixmap) {
    def size: Vec2[Int] = Vec2(width, height)
    def width: Int = tex.getWidth
    def height: Int = tex.getHeight
  }

  implicit class TextureRegionSizeImplicits(tex: TextureRegion) {
    def size: Vec2[Int] = Vec2(width, height)
    def width: Int = tex.getRegionWidth
    def height: Int = tex.getRegionHeight
    def x: Int = tex.getRegionX
    def y: Int = tex.getRegionY
    def pos: Vec2[Int] = Vec2(x, y)
    def bounds: Box2[Int] = Box2(ll = pos, size = size)
  }

}

object ImageImplicits extends ImageImplicits

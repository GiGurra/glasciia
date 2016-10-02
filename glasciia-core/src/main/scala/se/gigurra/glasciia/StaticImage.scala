package se.gigurra.glasciia

import java.io.FileNotFoundException

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import se.gigurra.glasciia.util.LoadFile
import se.gigurra.math.Vec2

case class StaticImage(region: TextureRegion)  {

  def subPixels(x: Int, y: Int, width: Int, height: Int): StaticImage = {
    StaticImage(new TextureRegion(region, x, y, width, height))
  }

  def subFraction(x: Float, y: Float, width: Float, height: Float): StaticImage = {
    val uAbs = u + x * uuSize
    val vAbs = v + y * vvSize
    StaticImage(new TextureRegion(
      region.getTexture,
      uAbs,
      vAbs,
      uAbs + width * uuSize,
      vAbs + height * vvSize
    ))
  }

  def width: Int = region.getRegionWidth
  def height: Int = region.getRegionHeight
  def size: Vec2[Int] = Vec2(region.getRegionWidth, region.getRegionHeight)

  def u: Float = region.getU
  def u2: Float = region.getU2
  def v: Float = region.getV
  def v2: Float = region.getV2
  def uuSize: Float = u2 - u
  def vvSize: Float = v2 - v
}

object StaticImage {

  def apply(location: String, useMipMaps: Boolean = true): StaticImage = {
    val fileHandle = LoadFile(location).getOrElse(throw new FileNotFoundException(s"Could not find image file '$location'"))
    new StaticImage(new TextureRegion(new Texture(fileHandle, useMipMaps)))
  }

  import scala.language.implicitConversions
  implicit def img2region(img: StaticImage): TextureRegion = img.region
}

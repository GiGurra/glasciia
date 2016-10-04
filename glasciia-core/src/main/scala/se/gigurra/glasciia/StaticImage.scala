package se.gigurra.glasciia

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
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

  def apply(fileHandle: FileHandle,
            useMipMaps: Boolean = true,
            minFilter: Texture.TextureFilter = Texture.TextureFilter.MipMapLinearLinear,
            magFilter: Texture.TextureFilter = Texture.TextureFilter.Linear): StaticImage = {
    val texture = new Texture(fileHandle, useMipMaps)
    texture.setFilter(minFilter, magFilter)
    new StaticImage(new TextureRegion(texture))
  }

  import scala.language.implicitConversions
  implicit def img2region(img: StaticImage): TextureRegion = img.region
}

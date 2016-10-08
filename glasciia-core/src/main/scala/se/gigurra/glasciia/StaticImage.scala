package se.gigurra.glasciia

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import se.gigurra.math.Vec2

case class StaticImage(region: TextureRegion) extends Image {

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

}

object StaticImage {

  def fromFile(fileHandle: FileHandle,
               useMipMaps: Boolean = true,
               minFilter: Texture.TextureFilter = Texture.TextureFilter.MipMapLinearLinear,
               magFilter: Texture.TextureFilter = Texture.TextureFilter.Linear): StaticImage = {
    val texture = new Texture(fileHandle, useMipMaps)
    texture.setFilter(minFilter, magFilter)
    fromRegion(new TextureRegion(texture))
  }

  def fromRegion(region: TextureRegion): StaticImage = {
    new StaticImage(region)
  }
}

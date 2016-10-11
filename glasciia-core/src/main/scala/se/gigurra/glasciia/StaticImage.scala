package se.gigurra.glasciia

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.{Pixmap, Texture}
import com.badlogic.gdx.graphics.g2d.TextureRegion

object StaticImage {

  def fromFile(fileHandle: FileHandle,
               useMipMaps: Boolean = true,
               minFilter: Texture.TextureFilter = Texture.TextureFilter.MipMapLinearLinear,
               magFilter: Texture.TextureFilter = Texture.TextureFilter.Linear): TextureRegion = {
    val texture = new Texture(fileHandle, useMipMaps)
    texture.setFilter(minFilter, magFilter)
    new TextureRegion(texture)
  }

  def fromPixMap(pixMap: Pixmap,
                 useMipMaps: Boolean = true,
                 minFilter: Texture.TextureFilter = Texture.TextureFilter.MipMapLinearLinear,
                 magFilter: Texture.TextureFilter = Texture.TextureFilter.Linear): TextureRegion = {
    val texture = new Texture(pixMap, useMipMaps)
    texture.setFilter(minFilter, magFilter)
    new TextureRegion(texture)
  }
}

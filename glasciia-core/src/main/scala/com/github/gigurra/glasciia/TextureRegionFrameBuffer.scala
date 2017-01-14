package com.github.gigurra.glasciia

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.{Pixmap, Texture}
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.github.gigurra.glasciia.Glasciia._

/**
  * Created by johan on 2017-01-14.
  */
case class TextureRegionFrameBuffer(region: TextureRegion,
                                    useDepth: Boolean,
                                    useStencil: Boolean,
                                    useHdpi: Boolean = true)
  extends FrameBuffer(
    region.getTexture.getTextureData.getFormat,
    region.width,
    region.height,
    useDepth,
    useStencil
  ) {

  def glFormat: Int = {
    Pixmap.Format.toGlFormat(format)
  }

  def glType: Int = {
    Pixmap.Format.toGlType(format)
  }

  def use(content: => Unit): Unit = {
    begin()
    content
    end()
  }

  override protected def createColorTexture: Texture = {
    region.getTexture
  }

  override protected def disposeColorTexture(colorTexture: Texture): Unit = {
    // Don't dispose anything
  }

  override protected def setFrameBufferViewport(): Unit = {
    Gdx.gl20.glViewport(region.x, region.y, region.width, region.height)
  }
}

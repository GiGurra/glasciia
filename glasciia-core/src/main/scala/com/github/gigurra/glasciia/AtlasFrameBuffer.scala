package com.github.gigurra.glasciia

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture.TextureWrap
import com.badlogic.gdx.graphics.g2d.SpriteBatcher
import com.badlogic.gdx.graphics.glutils.{FrameBuffer, GLOnlyTextureData}
import com.badlogic.gdx.graphics.{Camera, Pixmap, Texture}
import com.github.gigurra.glasciia.Glasciia._
import com.github.gigurra.math.Box2

/**
  * Created by johan on 2017-01-14.
  */
class AtlasFrameBuffer(width: Int,
                       height: Int,
                       format: Pixmap.Format = Pixmap.Format.RGBA8888,
                       val useDepth: Boolean = false,
                       val useStencil: Boolean = false,
                       val textureConf: TextureConf = TextureConf())
  extends FrameBuffer(
    format,
    width,
    height,
    useDepth,
    useStencil
  ) {

  def glFormat: Int = {
    Pixmap.Format.toGlFormat(format)
  }

  def glType: Int = {
    Pixmap.Format.toGlType(format)
  }

  def clearBits: Int = {
    var out = GL_COLOR_BUFFER_BIT
    if (useDepth)
      out |= GL_DEPTH_BUFFER_BIT
    if (useStencil)
      out |= GL_STENCIL_BUFFER_BIT
    out
  }

  def texture: Texture = {
    getColorBufferTexture
  }

  def use(region: Box2,
          batch: SpriteBatcher,
          projection: Camera,
          clear: Boolean = true)(content: => Unit): Unit = {

    // Preparation code

    val prevScissorBox = batch.getScissorBox
    val prevViewport = batch.getViewport
    val wasScissorsEnabled = Gdx.gl.glIsEnabled(GL_SCISSOR_TEST)
    val wasDrawingBefore = batch.isDrawing

    batch.setProjectionMatrix(projection.combined) // also calls batch.flush()

    if (!wasScissorsEnabled) Gdx.gl.glEnable(GL_SCISSOR_TEST)
    if (!wasDrawingBefore) batch.begin()
    batch.setViewport(region)
    batch.setScissorBox(region)

    // Fbo and rendering code

    bind()
    if (clear)
      Gdx.gl.glClear(clearBits)

    content

    end(0, 0, Gdx.graphics.getBackBufferWidth, Gdx.graphics.getBackBufferHeight)


    // Cleanup code

    if (!wasDrawingBefore) batch.end()
    if (!wasScissorsEnabled) Gdx.gl.glDisable(GL_SCISSOR_TEST)
    batch.setViewport(prevViewport)
    batch.setScissorBox(prevScissorBox)
  }

  override def begin(): Unit = {
    throw new RuntimeException(s"Not supported. Call 'use(..){..}' instead")
  }

  override protected def createColorTexture: Texture = {
    val data = new GLOnlyTextureData(width, height, 0, glFormat, glFormat, glType)
    val result = new Texture(data)
    result.setFilter(textureConf.minFilter, textureConf.magFilter)
    result.setWrap(TextureWrap.ClampToEdge, TextureWrap.ClampToEdge)
    result
  }
}

package com.github.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.HdpiUtils
import com.badlogic.gdx.graphics.{Color, OrthographicCamera}
import com.github.gigurra.glasciia.Glasciia._
import com.github.gigurra.math.{Box2, Vec2}

/**
  * Created by johan on 2016-10-01.
  */
trait FrameDrawer {

  def batch: Batch
  def screenBounds: Box2[Int]
  def camera: OrthographicCamera
  def transform: Matrix4Stack

  def drawFrame(pixelViewport: Box2[Int] = screenBounds,
                clearBuffer: Option[Color] = Some(Color.BLACK),
                camPos: Vec2[Float] = camera.position,
                camViewportWithoutZoom: Vec2[Float] = Vec2(camera.viewportWidth, camera.viewportHeight),
                yDown: Boolean = false,
                setOrtho: Boolean = true,
                useBatch: Boolean = true)(content: => Unit): Unit = {
    clearBuffer foreach { color =>
      gl.glClearColor(color.r, color.g, color.b, color.a)
      gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
    }
    drawSubFrame(
      pixelViewport = pixelViewport,
      camPos = camPos,
      camViewportWithoutZoom = camViewportWithoutZoom,
      yDown = yDown,
      setOrtho = setOrtho,
      useBatch = useBatch
    )(content)
  }

  def drawSubFrame(pixelViewport: Box2[Int],
                   camPos: Vec2[Float] = camera.position,
                   camViewportWithoutZoom: Vec2[Float] = Vec2(camera.viewportWidth, camera.viewportHeight),
                   yDown: Boolean = false,
                   setOrtho: Boolean = true,
                   useBatch: Boolean = true)(content: => Unit): Unit = {
    if (setOrtho)
      camera.setToOrtho(yDown, camViewportWithoutZoom.x, camViewportWithoutZoom.y)
    camera.position.set(camPos)
    camera.update()
    HdpiUtils.glViewport(
      pixelViewport.ll.x,
      pixelViewport.ll.y,
      pixelViewport.width,
      pixelViewport.height
    )
    batch.setProjectionMatrix(camera.combined)
    transform.pushPop {
      if (useBatch)
        batch.begin()
      content
      if (useBatch)
        batch.end()
    }
  }
}

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
  def screenBounds: Box2
  def camera: OrthographicCamera

  def drawFrame(pixelViewport: Box2 = screenBounds,
                clearBuffer: Option[Color] = Some(Color.BLACK),
                camPos: Vec2 = camera.position,
                camViewportWithoutZoom: Vec2 = Vec2(camera.viewportWidth, camera.viewportHeight),
                camZoom: Float = camera.zoom,
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
      camZoom = camZoom,
      yDown = yDown,
      setOrtho = setOrtho,
      useBatch = useBatch
    )(content)
  }

  def drawSubFrame(pixelViewport: Box2,
                   camPos: Vec2 = camera.position,
                   camViewportWithoutZoom: Vec2 = Vec2(camera.viewportWidth, camera.viewportHeight),
                   camZoom: Float = camera.zoom,
                   yDown: Boolean = false,
                   setOrtho: Boolean = true,
                   useBatch: Boolean = true)(content: => Unit): Unit = {

    if (setOrtho)
      camera.setToOrtho(yDown, camViewportWithoutZoom.x, camViewportWithoutZoom.y)

    camera.zoom = camZoom
    camera.position.set(camPos)
    camera.update()

    HdpiUtils.glViewport(
      pixelViewport.ll.x.toInt,
      pixelViewport.ll.y.toInt,
      pixelViewport.width.toInt,
      pixelViewport.height.toInt
    )

    batch.setProjectionMatrix(camera.combined)

    if (useBatch)
      batch.begin()

    content

    if (useBatch)
      batch.end()
  }
}

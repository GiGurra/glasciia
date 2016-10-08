package se.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.{Camera, Color}
import se.gigurra.glasciia.Glasciia
import se.gigurra.math.{Box2, Vec2}
import Glasciia._
import com.badlogic.gdx.graphics.glutils.HdpiUtils

/**
  * Created by johan on 2016-10-01.
  */
trait FrameDrawer {

  def batch: Batch
  def camera: Camera
  def transform: Matrix4Stack

  def drawFrame(screenBounds: Box2[Int], background: Color = Color.BLACK, camPos: Vec2[Float] = camera.position)(content: => Unit): Unit = {
    gl.glClearColor(background.r, background.g, background.b, background.a)
    gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
    drawSubFrame(screenBounds, camPos)(content)
  }

  def drawSubFrame(screenBounds: Box2[Int], camPos: Vec2[Float] = camera.position)(content: => Unit): Unit = {
    camera.position.set(camPos)
    camera.update()
    HdpiUtils.glViewport(
      screenBounds.ll.x,
      screenBounds.ll.y,
      screenBounds.width,
      screenBounds.height
    )
    batch.setProjectionMatrix(camera.combined)
    transform.pushPop {
      content
      if (batch.isDrawing)
        batch.end()
    }
  }
}

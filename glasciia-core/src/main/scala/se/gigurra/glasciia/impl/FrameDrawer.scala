package se.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.{Camera, Color}
import se.gigurra.glasciia.Glasciia
import se.gigurra.math.Vec2
import Glasciia._

/**
  * Created by johan on 2016-10-01.
  */
trait FrameDrawer {

  def batch: Batch
  def camera: Camera
  def transform: Matrix4Stack

  def drawFrame(background: Color, camPos: Vec2[Float] = camera.position)(content: => Unit): Unit = {
    gl.glClearColor(background.r, background.g, background.b, background.a)
    gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
    drawSubFrame(content, camPos)
  }

  def drawSubFrame(content: => Unit, camPos: Vec2[Float] = camera.position): Unit = {
    camera.position.set(camPos)
    camera.update()
    batch.setProjectionMatrix(camera.combined)
    transform.pushPop {
      content
      if (batch.isDrawing)
        batch.end()
    }
  }
}

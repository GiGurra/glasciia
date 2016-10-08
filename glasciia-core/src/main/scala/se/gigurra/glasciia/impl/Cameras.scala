package se.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.{Camera, OrthographicCamera}
import com.badlogic.gdx.math.Vector3
import se.gigurra.glasciia.Canvas
import se.gigurra.math.Vec2

/**
  * Created by johan on 2016-10-01.
  */
trait Cameras { canvas: Canvas =>

  val orthographicCamera = new OrthographicCamera
  var camera: Camera = orthographicCamera

  def setOrtho(yDown: Boolean, width: Float, height: Float): Unit = {
    orthographicCamera.setToOrtho(yDown, width, height)
    camera = orthographicCamera
  }

  def screen2World(screenPos: Vec2[Int]): Vec2[Float] = {
    val out = new Vector3(screenPos.x, screenPos.y, 0.0f)
    camera.unproject(out, 0.0f, 0.0f, canvas.width, canvas.height)
    Vec2[Float](out.x, out.y)
  }

  def world2Screen(screenPos: Vec2[Float]): Vec2[Int] = {
    val out = new Vector3(screenPos.x, screenPos.y, 0.0f)
    camera.project(out, 0.0f, 0.0f, canvas.width, canvas.height)
    Vec2[Int](out.x.toInt, out.y.toInt)
  }
}

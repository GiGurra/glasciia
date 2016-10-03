package se.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.{Camera, OrthographicCamera}
import com.badlogic.gdx.math.Vector3
import se.gigurra.math.Vec2

/**
  * Created by johan on 2016-10-01.
  */
trait Cameras {

  val orthographicCamera = new OrthographicCamera
  var camera: Camera = orthographicCamera

  def setOrtho(yDown: Boolean, width: Float, height: Float): Unit = {
    orthographicCamera.setToOrtho(yDown, width, height)
    camera = orthographicCamera
  }

  def screen2World(screenPos: Vec2[Int]): Vec2[Float] = {
    val out = new Vector3(screenPos.x, screenPos.y, 0.0f)
    camera.unproject(out, 0.0f, 0.0f, camera.viewportWidth, camera.viewportHeight)
    Vec2[Float](out.x, out.y)
  }
}

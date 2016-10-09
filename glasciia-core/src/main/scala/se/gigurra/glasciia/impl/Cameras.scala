package se.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.{Camera, OrthographicCamera}
import com.badlogic.gdx.math.Vector3
import se.gigurra.math.{Box2, Vec2, Zero}

/**
  * Created by johan on 2016-10-01.
  */
trait Cameras {

  val camera = new OrthographicCamera

  def setOrtho(yDown: Boolean, width: Float, height: Float): Unit = {
    camera.setToOrtho(yDown, width, height)
  }

  def setOrtho(yDown: Boolean, width: Float, height: Float, scaling: Float): Unit = {
    camera.setToOrtho(yDown, width / scaling, height / scaling)
  }

  def screen2World(screenPos: Vec2[Int], projectionArea: Box2[Float]): Vec2[Float] = {
    val out = new Vector3(screenPos.x, screenPos.y, 0.0f)
    camera.unproject(out, projectionArea.ll.x, projectionArea.ll.y, projectionArea.width, projectionArea.height)
    Vec2[Float](out.x, out.y)
  }

  def world2Screen(screenPos: Vec2[Float], projectionArea: Box2[Float]): Vec2[Int] = {
    val out = new Vector3(screenPos.x, screenPos.y, 0.0f)
    camera.project(out, projectionArea.ll.x, projectionArea.ll.y, projectionArea.width, projectionArea.height)
    Vec2[Int](out.x.toInt, out.y.toInt)
  }
}

package se.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.{Camera, OrthographicCamera, PerspectiveCamera}

/**
  * Created by johan on 2016-10-01.
  */
trait Cameras {

  val orthographicCamera = new OrthographicCamera
  val perspectiveCamera = new PerspectiveCamera
  var camera: Camera = orthographicCamera

  def setOrtho(yDown: Boolean, width: Float, height: Float): Unit = {
    orthographicCamera.setToOrtho(yDown, width, height)
    camera = orthographicCamera
  }
}

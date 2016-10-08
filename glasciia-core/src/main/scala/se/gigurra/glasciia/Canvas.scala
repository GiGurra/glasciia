package se.gigurra.glasciia

import se.gigurra.glasciia.impl._
import se.gigurra.math.Vec2

/**
  * Created by johan on 2016-09-29.
  */
case class Canvas(app: App)
    extends DefaultShader
    with Batcher
    with Cameras
    with ContentDrawer
    with FrameDrawer
    with TextDrawer
    with ImageDrawer
    with AnimationDrawer
    with ParticleDrawer
    with MouseFunctions
    with BackgroundDrawer {

  def size: Vec2[Int] = app.size
  def width: Int = app.width
  def height: Int = app.height
  def aspectRatio: Float = width.toFloat / height.toFloat

  def drawTime: Double = _drawTimeSeconds

  /**
    * Used by time dependent drawing, e.g. animations
    */
  protected[glasciia] def setDrawTime(now: Double = app.localAppTime): Unit = {
    _drawTimeSeconds = now
  }

  private var _drawTimeSeconds: Double = app.localAppTime
}

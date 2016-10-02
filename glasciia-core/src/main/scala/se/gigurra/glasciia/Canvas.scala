package se.gigurra.glasciia

import se.gigurra.glasciia.impl._
import se.gigurra.math.Vec2

/**
  * Created by johan on 2016-09-29.
  */
case class Canvas(app: App)
  extends Glasciia
    with DefaultShader
    with Batcher
    with Cameras
    with ContentDrawer
    with FrameDrawer
    with TextDrawer
    with StaticImageDrawer
    with AnimationDrawer
    with ParticleEffectDrawer {

  def size: Vec2[Int] = app.size
  def width: Int = app.width
  def height: Int = app.height
}

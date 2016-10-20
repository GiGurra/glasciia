package com.github.gigurra.glasciia

import com.github.gigurra.glasciia.impl._
import com.github.gigurra.math.{Box2, Vec2, Zero}

/**
  * Created by johan on 2016-09-29.
  */
case class Canvas(game: Game)
    extends Batcher
    with Cameras
    with ContentDrawer
    with FrameDrawer
    with TextDrawer
    with ImageDrawer
    with AnimationDrawer
    with ParticleDrawer
    with MouseFunctions
    with BackgroundDrawer
    with GuiDrawer {

  def size: Vec2[Int] = game.size
  def screenBounds: Box2[Int] = Box2[Int](0,0,width,height)
  def width: Int = game.width
  def height: Int = game.height
  def aspectRatio: Float = width.toFloat / height.toFloat

  def drawTime: Double = _drawTimeSeconds

  def screen2World(screenPos: Vec2[Int]): Vec2[Float] = screen2World(screenPos, wholeCanvasProjectionArea)
  def world2Screen(screenPos: Vec2[Float]): Vec2[Int] = world2Screen(screenPos, wholeCanvasProjectionArea)

  def wholeCanvasProjectionArea: Box2[Float] = Box2[Float](ll = Zero.vec2f, size = Vec2[Float](width, height))

  /**
    * Used by time dependent drawing, e.g. animations
    */
  protected[glasciia] def setDrawTime(now: Double = game.localAppTime): Unit = {
    _drawTimeSeconds = now
  }

  private var _drawTimeSeconds: Double = game.localAppTime
}

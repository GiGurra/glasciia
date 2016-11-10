package com.github.gigurra.glasciia

import com.badlogic.gdx.Gdx
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
    with PolygonDrawer
    with AnimationDrawer
    with ParticleDrawer
    with MouseFunctions
    with BackgroundDrawer
    with GuiDrawer {

  def width: Int = Gdx.graphics.getWidth
  def height: Int = Gdx.graphics.getHeight
  def orientation: Orientation = if (width >= height) Orientation.Landscape else Orientation.Portrait
  def size: Vec2[Int] = Vec2(width, height)
  def screenBounds: Box2[Int] = Box2[Int](0, 0, width, height)
  def aspectRatio: Float = width.toFloat / height.toFloat

  def drawTime: Long = _drawTimeMillis

  def screen2World(screenPos: Vec2[Int]): Vec2[Float] = screen2World(screenPos, wholeCanvasProjectionArea)
  def world2Screen(screenPos: Vec2[Float]): Vec2[Int] = world2Screen(screenPos, wholeCanvasProjectionArea)

  def wholeCanvasProjectionArea: Box2[Float] = Box2[Float](ll = Zero.vec2f, size = Vec2[Float](width, height))

  /**
    * Used by time dependent drawing, e.g. animations
    */
  protected[glasciia] def setDrawTime(now: Long = System.nanoTime / 1000000L - game.t0): Unit = {
    _drawTimeMillis = now
  }

  private var _drawTimeMillis: Long = 0L
}

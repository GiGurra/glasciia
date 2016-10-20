package com.github.gigurra.glasciia

import com.badlogic.gdx.Gdx
import com.github.gigurra.glasciia.impl.{ApplicationEventListener, ResourceManager}
import com.github.gigurra.math.Vec2

import scala.util.control.NonFatal

/**
  * Created by johan on 2016-09-19.
  */
abstract class Game extends ApplicationEventListener with ResourceManager {

  private val t0 = System.nanoTime / 1e9

  def width: Int = Gdx.graphics.getWidth
  def height: Int = Gdx.graphics.getHeight
  def size: Vec2[Int] = Vec2(width, height)
  def localAppTime: Double = System.nanoTime / 1e9 - t0
  def close(): Unit = Gdx.app.exit()
  override def canvas: Canvas = Option(super.canvas).getOrElse(throw new IllegalAccessError(s"Cannot access canvas before startup has finished"))

  def eventHandler: PartialFunction[GameEvent, Unit]
}

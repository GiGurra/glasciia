package com.github.gigurra.glasciia

import com.badlogic.gdx.Gdx
import com.github.gigurra.glasciia.impl.ResourceManager
import com.github.gigurra.math.Vec2

/**
  * Created by johan on 2016-09-19.
  * Must be used with a GameLauncher
  */
abstract class Game extends ResourceManager {

  val t0: Double = System.nanoTime / 1e9
  val canvas: Canvas = Canvas(this)

  def eventHandler: PartialFunction[GameEvent, Unit]

  def width: Int = Gdx.graphics.getWidth
  def height: Int = Gdx.graphics.getHeight
  def size: Vec2[Int] = Vec2(width, height)
  def localAppTime: Double = System.nanoTime / 1e9 - t0
  def close(): Unit = Gdx.app.exit()

  private[glasciia] def consume(ev: GameEvent): Boolean = {
    eventHandler.lift.apply(ev) match {
      case Some(_) => true
      case None => false
    }
  }
}

package com.github.gigurra.glasciia

import com.badlogic.gdx.Gdx

/**
  * Created by johan on 2016-09-19.
  * Must be used with a GameLauncher
  */
abstract class Game {

  val t0: Long = System.nanoTime / 1000000L
  val canvas: Canvas = Canvas(this)

  def eventHandler: PartialFunction[GameEvent, Unit]
  def close(): Unit = Gdx.app.exit()
  def time: Long = canvas.drawTime

  private lazy val liftedEventHandler = eventHandler.lift

  private[glasciia] def consume(ev: GameEvent): Boolean = {
    liftedEventHandler.apply(ev) match {
      case Some(_) => true
      case None => false
    }
  }
}

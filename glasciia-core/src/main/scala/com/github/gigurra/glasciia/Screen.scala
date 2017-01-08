package com.github.gigurra.glasciia

/**
  * Created by johan on 2017-01-08.
  */
trait Screen {
  def eventHandler: PartialFunction[GameEvent, Unit]
}

abstract class GameScreen[T <: Game](game: T) extends Screen {
  def time: Long = game.time
  def canvas: Canvas = game.canvas
}

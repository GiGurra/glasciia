package com.github.gigurra.glasciia

/**
  * Created by johan on 2017-01-08.
  */
trait Screen {
  def eventHandler: PartialFunction[GameEvent, Unit]
}

abstract class GameScreen[T <: Game](val canvas: Canvas) extends Screen {
}

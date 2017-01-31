package com.github.gigurra.glasciia

/**
  * Created by johan on 2017-01-08.
  */
trait Screen {
  def eventHandler: PartialFunction[GameEvent, Unit]
}

package com.github.gigurra.glasciia

/**
  * Created by johan on 2016-10-31.
  */
trait Scene extends EventHandler {
  def begun: Boolean
  def finished: Boolean
  def timeLeft: Option[Long]
  def eventHandler: PartialFunction[GameEvent, Unit]
}

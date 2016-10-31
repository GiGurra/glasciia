package com.github.gigurra.glasciia

import scala.language.implicitConversions

/**
  * Created by johan on 2016-10-31.
  */
trait EventHandler {
  def eventHandler: PartialFunction[GameEvent, Unit]
}

object EventHandler {
  implicit def eventHandler2Pf(handler: EventHandler): PartialFunction[GameEvent, Unit] = {
    handler.eventHandler
  }
}

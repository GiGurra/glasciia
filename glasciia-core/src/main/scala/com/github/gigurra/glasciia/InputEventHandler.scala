package com.github.gigurra.glasciia

import com.github.gigurra.glasciia.GameEvent.InputEvent

import scala.language.implicitConversions

/**
  * Created by johan on 2016-10-31.
  */
trait InputEventHandler {
  def inputHandler: PartialFunction[InputEvent, Unit]
}

object InputEventHandler {
  implicit def eventHandler2Pf(handler: InputEventHandler): PartialFunction[InputEvent, Unit] = {
    handler.inputHandler
  }
}

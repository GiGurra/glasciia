package com.github.gigurra.glasciia

import com.github.gigurra.glasciia.GameEvent.InputEvent

/**
  * Created by johan on 2016-10-31.
  */
trait Scene extends InputEventHandler {

  def finished: Boolean

  def inputHandler: PartialFunction[InputEvent, Unit] = PartialFunction.empty

  def onEnd(): Unit = {}

  def update(elapsedInScene: Long): Unit = {
    _elapsedInScene = elapsedInScene
    if (finished && !_endCallbackCalled) {
      _endCallbackCalled = true
      onEnd()
    }
  }

  def begin(): Unit = {
    _begun = true
  }

  def begun: Boolean = {
    _begun
  }

  def elapsedInScene: Long = {
    _elapsedInScene
  }

  private var _begun = false
  private var _elapsedInScene = 0L
  private var _endCallbackCalled = false
}

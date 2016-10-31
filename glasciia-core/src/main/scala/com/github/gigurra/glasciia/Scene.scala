package com.github.gigurra.glasciia

import com.github.gigurra.glasciia.GameEvent.InputEvent

/**
  * Created by johan on 2016-10-31.
  */
trait Scene extends InputEventHandler {

  def finished: Boolean

  def inputHandler: PartialFunction[InputEvent, Unit] = PartialFunction.empty

  def onEnd(): Unit = {}

  def update(time: Long): Unit = {
    _time = time
    if (finished && !_endCallbackCalled) {
      _endCallbackCalled = true
      onEnd()
    }
  }

  def start(t0: Long): Unit = {
    _begun = true
    _t0 = t0
    _time = t0
  }

  def t0: Long = {
    _t0
  }

  def begun: Boolean = {
    _begun
  }

  def elapsed: Long = {
    _time - t0
  }

  private var _begun = false
  private var _t0 = 0L
  private var _time = 0L
  private var _endCallbackCalled = false
}

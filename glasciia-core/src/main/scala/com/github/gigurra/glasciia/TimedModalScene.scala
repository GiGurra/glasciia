package com.github.gigurra.glasciia

import com.github.gigurra.glasciia.GameEvent.InputEvent

/**
  * Created by johan on 2017-01-02.
  */
class TimedModalScene(overMillis: Long) extends TimedScene(overMillis) {

  protected def doUpdate(elapsedInScene: Long): Unit = {}
  protected def doOnEnd(): Unit = {}

  override final def update(elapsedInScene: Long): Unit = {
    doUpdate(elapsedInScene)
    super.update(elapsedInScene)
  }

  override final def onEnd(): Unit = {
    doOnEnd()
    super.onEnd()
  }

  override def inputHandler: PartialFunction[InputEvent, Unit] = {
    case _ => // Capture anything = be modal
  }
}

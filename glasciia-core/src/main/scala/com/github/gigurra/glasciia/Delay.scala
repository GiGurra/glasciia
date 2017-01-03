package com.github.gigurra.glasciia

import com.github.gigurra.glasciia.GameEvent.InputEvent
import com.github.gigurra.glasciia.TransitionSystem.Transition

/**
  * Created by johan on 2017-01-02.
  */
case class Delay(millis: Long, modal: Boolean = true) extends Transition {

  val act: Act = Act( new TimedScene(millis){
    override def inputHandler: PartialFunction[InputEvent, Unit] = {
      if (modal) {
        case _ =>
      } else {
        super.inputHandler
      }
    }
  })
}

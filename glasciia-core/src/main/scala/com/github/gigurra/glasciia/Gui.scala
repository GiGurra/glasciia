package com.github.gigurra.glasciia

import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.gigurra.glasciia.GameEvent.InputEvent
import com.github.gigurra.glasciia.Glasciia._

import scala.language.implicitConversions

/**
  * Created by johan on 2017-01-01.
  */
trait Gui {
  def stage: Stage
}

object Gui {

  implicit def gui2Stage(gui: Gui): Stage = {
    gui.stage
  }

  implicit def gui2InputHandler(gui: Gui): PartialFunction[InputEvent, Unit] = {
    gui.stage
  }
}

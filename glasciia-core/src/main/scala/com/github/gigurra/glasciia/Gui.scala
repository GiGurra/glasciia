package com.github.gigurra.glasciia

import com.badlogic.gdx.Gdx
import com.github.gigurra.glasciia.GameEvent.InputEvent

import scala.language.implicitConversions

/**
  * Created by johan on 2017-01-01.
  */
trait Gui {

  def draw(canvas: Canvas,
           dt: Float = Gdx.graphics.getDeltaTime,
           screenFitting: Scale = Scale.ONE,
           transform: Transform = Transform.IDENTITY): Unit

  def inputHandler: PartialFunction[InputEvent, Unit] = PartialFunction.empty
}

object Gui {
  implicit def gui2InputHandler(gui: Gui): PartialFunction[InputEvent, Unit] = {
    gui.inputHandler
  }
}

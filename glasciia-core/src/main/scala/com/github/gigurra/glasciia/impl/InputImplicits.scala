package com.github.gigurra.glasciia.impl

import com.badlogic.gdx.{Input, InputProcessor}
import com.github.gigurra.glasciia.GameEvent
import com.github.gigurra.glasciia.GameEvent._
import com.github.gigurra.math.Vec2

import scala.language.implicitConversions

/**
  * Created by johan on 2016-10-03.
  */
trait InputImplicits {
  import InputImplicitsImpl._

  implicit def input2InputEventCallbacks(input: Input): InputEventCallbacks = {
    new InputEventCallbacks(input)
  }
}

object InputImplicits extends InputImplicits

object InputImplicitsImpl {

  implicit class InputEventCallbacks(val input: Input) extends AnyVal {
    def setListener(listener: GameEvent => Boolean): Unit = {
      input.setInputProcessor(new InputProcessor {
        override def keyTyped(character: Char): Boolean = listener(CharTyped(character))
        override def keyDown(keycode: Int): Boolean = listener(KeyDown(keycode))
        override def keyUp(keycode: Int): Boolean = listener(KeyUp(keycode))
        override def mouseMoved(screenX: Int, screenY: Int): Boolean = listener(MouseMove(Vec2(screenX, screenY)))
        override def scrolled(amount: Int): Boolean = listener(MouseScrolled(amount))
        override def touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = listener(TouchDown(Vec2(screenX, screenY), pointer, button))
        override def touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = listener(TouchUp(Vec2(screenX, screenY), pointer, button))
        override def touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean = listener(TouchDrag(Vec2(screenX, screenY), pointer))
      })
    }
  }
}

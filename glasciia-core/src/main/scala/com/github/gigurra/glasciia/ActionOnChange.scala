package com.github.gigurra.glasciia

import com.badlogic.gdx.scenes.scene2d.Action

/**
  * Created by johan on 2017-01-31.
  */
class ActionOnChange[T](getter: => T, action: => Unit) extends Action {

  var oldvalue: T = _

  override def act(delta: Float): Boolean = {
    val newValue = getter
    if (newValue != oldvalue) {
      oldvalue = newValue
      action
    }
    false
  }
}

object ActionOnChange {
  def apply[T](getter: => T)(action: => Unit): ActionOnChange[T] = {
    new ActionOnChange[T](getter, action)
  }
}

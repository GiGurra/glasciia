package com.github.gigurra.glasciia

import com.badlogic.gdx.scenes.scene2d.Action

/**
  * Created by johan on 2017-01-31.
  */
class ActionOnChange[T](getter: => T, useFirst: Boolean, action: T => Unit) extends Action {

  private var oldvalue: T = _
  private var isFirst: Boolean = true

  override def act(delta: Float): Boolean = {
    val newValue = getter
    if (newValue != oldvalue) {
      oldvalue = newValue
      if (!isFirst || (isFirst && useFirst)) {
        action(newValue)
      }
    }
    isFirst = false
    false
  }
}

object ActionOnChange {
  def apply[T](getter: => T, useFirst: Boolean = false)(action: T => Unit): ActionOnChange[T] = {
    new ActionOnChange[T](getter, useFirst, action)
  }
}

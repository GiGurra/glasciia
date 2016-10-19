package com.github.gigurra.glasciia

import com.github.gigurra.math.Vec2

/**
  * Created by johan on 2016-09-26.
  */
sealed trait AppEvent
object AppEvent {
  sealed trait WindowEvent extends AppEvent
  case class Init(canvas: Canvas) extends WindowEvent
  case class Render(canvas: Canvas) extends WindowEvent
  case class Pause(canvas: Canvas) extends WindowEvent
  case class Resume(canvas: Canvas) extends WindowEvent
  case class Exit(canvas: Canvas) extends WindowEvent
  case class Resize(newSize: Vec2[Int], canvas: Canvas) extends WindowEvent

  sealed trait InputEvent extends AppEvent
  case object ConsumedEvent extends InputEvent

  sealed trait KeyboardEvent extends InputEvent
  case class CharTyped(char: Char) extends KeyboardEvent
  case class KeyDown(vKey: Int) extends KeyboardEvent
  case class KeyUp(vKey: Int) extends KeyboardEvent

  sealed trait ControllerEvent extends InputEvent { def controller: Controller }
  case class AxisMoved(controller: Controller, axis: Int, newValue: Float, oldValue: Float) extends ControllerEvent
  case class ButtonDown(controller: Controller, button: Int) extends ControllerEvent
  case class ButtonUp(controller: Controller, button: Int) extends ControllerEvent

  sealed trait MouseEvent extends InputEvent
  case class MouseMove(pos: Vec2[Int]) extends MouseEvent
  case class MouseScrolled(amount: Int) extends MouseEvent
  case class TouchDown(pos: Vec2[Int], ptr: Int, btn: Int) extends MouseEvent
  case class TouchUp(pos: Vec2[Int], ptr: Int, btn: Int) extends MouseEvent
  case class TouchDrag(pos: Vec2[Int], ptr: Int) extends MouseEvent
}

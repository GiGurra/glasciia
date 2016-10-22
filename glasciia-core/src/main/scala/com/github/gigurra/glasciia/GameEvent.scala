package com.github.gigurra.glasciia

import com.github.gigurra.math.Vec2

/**
  * Created by johan on 2016-09-26.
  */
sealed trait GameEvent
object GameEvent {
  sealed trait WindowEvent extends GameEvent { def canvas: Canvas; def game: Game = canvas.game }
  case class Init(canvas: Canvas) extends WindowEvent
  case class Render(canvas: Canvas) extends WindowEvent
  case class Pause(canvas: Canvas) extends WindowEvent
  case class Resume(canvas: Canvas) extends WindowEvent
  case class Exit(canvas: Canvas) extends WindowEvent
  case class Resize(newSize: Vec2[Int], canvas: Canvas) extends WindowEvent

  sealed trait InputEvent extends GameEvent
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

  sealed trait GestureEvent extends GameEvent
  case class GestureTouchDown(pos: Vec2[Float], pointer: Int, button: Int) extends GestureEvent
  case class GestureTap(pos: Vec2[Float], count: Int, button: Int) extends GestureEvent
  case class GestureLongPress(pos: Vec2[Float]) extends GestureEvent
  case class GestureFling(velocity: Vec2[Float], button: Int) extends GestureEvent
  case class GesturePan(pos: Vec2[Float], delta: Vec2[Float]) extends GestureEvent
  case class GesturePanStop(pos: Vec2[Float], pointer: Int, button: Int) extends GestureEvent
  case class GestureZoom(initialDistance: Float, distance: Float) extends GestureEvent
  case class GesturePinch(initialPointer1: Vec2[Float], initialPointer2: Vec2[Float], pointer1: Vec2[Float], pointer2: Vec2[Float]) extends GestureEvent
  case class GesturePinchStop() extends GestureEvent
}

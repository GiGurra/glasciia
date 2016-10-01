package se.gigurra.glasciia

import se.gigurra.math.Vec2

/**
  * Created by johan on 2016-09-26.
  */
sealed trait ApplicationEvent
object ApplicationEvent {
  sealed trait WindowEvent extends ApplicationEvent
  case class Init(canvas: Canvas) extends WindowEvent
  case class Render(canvas: Canvas) extends WindowEvent
  case class Pause(canvas: Canvas) extends WindowEvent
  case class Resume(canvas: Canvas) extends WindowEvent
  case class Exit(canvas: Canvas) extends WindowEvent
  case class Resize(newSize: Vec2[Int], canvas: Canvas) extends WindowEvent

  sealed trait KeyboardEvent extends ApplicationEvent
  case class CharTyped(char: Char) extends KeyboardEvent
  case class KeyDown(vKey: Int) extends KeyboardEvent
  case class KeyUp(vKey: Int) extends KeyboardEvent

  sealed trait MouseEvent extends ApplicationEvent
  case class MouseMove(pos: Vec2[Int]) extends MouseEvent
  case class MouseScrolled(amount: Int) extends MouseEvent
  case class TouchDown(pos: Vec2[Int], ptr: Int, btn: Int) extends MouseEvent
  case class TouchUp(pos: Vec2[Int], ptr: Int, btn: Int) extends MouseEvent
  case class TouchDrag(pos: Vec2[Int], ptr: Int) extends MouseEvent
}

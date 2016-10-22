package com.github.gigurra.glasciia

import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.input.GestureDetector.GestureListener
import com.badlogic.gdx.math.Vector2
import com.github.gigurra.glasciia.GameEvent._
import com.github.gigurra.glasciia.Glasciia._
import com.github.gigurra.math.Vec2

/**
  * Created by johan on 2016-10-22.
  */
case class GestureState(halfTapSquareSize: Float = 20.0f,
                        tapCountInterval: Float = 0.4f,
                        longPressDuration: Float = 1.1f,
                        maxFlingDelay: Float = 0.15f) {

  private var mappings = PartialFunction.empty[GestureEvent, Unit]
  private val detector = new GestureDetector(halfTapSquareSize, tapCountInterval, longPressDuration, maxFlingDelay, new GestureListener {
    def consume(event: GestureEvent): Boolean = {
      mappings.lift.apply(event) match {
        case Some(_) => true
        case None => false
      }
    }
    override def touchDown(x: Float, y: Float, pointer: Int, button: Int): Boolean = consume(GestureTouchDown(Vec2(x,y), pointer = pointer, button = button))
    override def longPress(x: Float, y: Float): Boolean = consume(GestureLongPress(Vec2(x, y)))
    override def zoom(initialDistance: Float, distance: Float): Boolean = consume(GestureZoom(initialDistance = initialDistance, distance = distance))
    override def pan(x: Float, y: Float, deltaX: Float, deltaY: Float): Boolean = consume(GesturePan(pos = Vec2(x,y), delta = Vec2(deltaX, deltaY)))
    override def tap(x: Float, y: Float, count: Int, button: Int): Boolean = consume(GestureTap(Vec2(x,y), count = count, button = button))
    override def fling(velocityX: Float, velocityY: Float, button: Int): Boolean = consume(GestureFling(velocity = Vec2(velocityX, velocityY), button = button))
    override def panStop(x: Float, y: Float, pointer: Int, button: Int): Boolean = consume(GesturePanStop(pos = Vec2(x,y), pointer = pointer, button = button))
    override def pinch(initialPointer1: Vector2, initialPointer2: Vector2, pointer1: Vector2, pointer2: Vector2): Boolean = consume(GesturePinch(initialPointer1 = initialPointer1, initialPointer2 = initialPointer2, pointer1 = pointer1, pointer2 = pointer2))
    override def pinchStop(): Unit = consume(GesturePinchStop())
  })

  def toInputProcessor(mappings: PartialFunction[GestureEvent, Unit]): InputProcessor = {
    this.mappings = mappings
    detector
  }
}

object GestureState {
  val GLOBAL = new GestureState()
}

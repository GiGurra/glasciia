package com.github.gigurra.glasciia.impl

import com.badlogic.gdx.scenes.scene2d._
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.github.gigurra.glasciia.GameEvent
import com.github.gigurra.glasciia.GameEvent.{CharTyped, KeyDown, KeyUp, KeyboardEvent}
import scala.language.implicitConversions
/**
  * Created by johan on 2016-10-09.
  */
trait StageImplicits {

  implicit def stageCanConsumeEvents[T <: Stage](self: T): StageImplicitsImpl.canConsumeEvents[T] = {
    new StageImplicitsImpl.canConsumeEvents(self)
  }
}

object StageImplicits extends StageImplicits

object StageImplicitsImpl {

  implicit class canConsumeEvents[T <: Stage] (val self: T) extends AnyVal {
    def on[R](f: PartialFunction[GameEvent.InputEvent, R], consume: Boolean = true): InputListener = addAndReturnListener(keyListener(f, consume))

    def onClick(f: (Stage, Float, Float) => Unit): InputListener = addAndReturnListener(clickListener((x, y) => f(self, x,y)))
    def onClick(f: (Float, Float) => Unit): InputListener = onClick((_: Stage, x: Float, y: Float) => f(x,y))
    def onClick(f: (Stage => Unit)): InputListener = onClick((receiver: Stage, _: Float, _: Float) => f(receiver))
    def onClick(f: => Unit): InputListener = onClick((_: Float, _: Float) => f)

    def blockInputEventPropagation(): InputListener = addAndReturnListener(new InputListener{
      override def handle(e: Event): Boolean = {
        super.handle(e)
        true
      }
    })

    private def addAndReturnListener[L <: EventListener](listener: L): L ={
      self.addListener(listener)
      listener
    }
  }

  private def clickListener(f: (Float, Float) => Unit): InputListener = new ClickListener {
    override def clicked(event: InputEvent, x: Float, y: Float): Unit = f(x, y)
  }

  private def keyListener[R](f: PartialFunction[KeyboardEvent, R], consume: Boolean): InputListener = new InputListener {
    private val lifted: (GameEvent.KeyboardEvent) => Option[R] = f.lift
    override def keyDown(event: InputEvent, keycode: Int): Boolean = tryConsume(lifted, KeyDown(keycode)) && consume
    override def keyUp(event: InputEvent, keycode: Int): Boolean = tryConsume(lifted, KeyUp(keycode)) && consume
    override def keyTyped(event: InputEvent, character: Char): Boolean = tryConsume(lifted, CharTyped(character)) && consume
  }

  private def tryConsume[R](lifted: (GameEvent.KeyboardEvent) => Option[R], event: GameEvent.KeyboardEvent): Boolean = {
    lifted.apply(event) match {
      case Some(_) => true
      case None => false
    }
  }
}
package com.github.gigurra.glasciia.impl

import com.badlogic.gdx.scenes.scene2d._
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener.FocusEvent
import com.badlogic.gdx.scenes.scene2d.utils.{ClickListener, FocusListener}
import com.github.gigurra.glasciia.GameEvent
import com.github.gigurra.glasciia.GameEvent.{CharTyped, KeyDown, KeyUp, KeyboardEvent}
import scala.language.implicitConversions
/**
  * Created by johan on 2016-10-09.
  */
trait ActorImplicits {

  implicit def actorCanTakeKeyboardFocus[T <: Actor](self: T): ActorImplicitsImpl.canTakeKeyboardFocus[T] = {
    new ActorImplicitsImpl.canTakeKeyboardFocus(self)
  }

  implicit def actorCanFireEvents[T <: Actor](self: T): ActorImplicitsImpl.canFireEvents[T] = {
    new ActorImplicitsImpl.canFireEvents(self)
  }

  implicit def actorCanFireAndReceiveEvents[T <: Actor](self: T): ActorImplicitsImpl.canFireAndReceiveEvents[T] = {
    new ActorImplicitsImpl.canFireAndReceiveEvents(self)
  }

  implicit def actorCanConsumeEvents[T <: Actor](self: T): ActorImplicitsImpl.canConsumeEvents[T] = {
    new ActorImplicitsImpl.canConsumeEvents(self)
  }
}

object ActorImplicits extends ActorImplicits

object ActorImplicitsImpl {

  implicit class canTakeKeyboardFocus[T <: Actor](val self: T) extends AnyVal {

    def setKeyFocus(): Unit = {
      self.getStage.setKeyboardFocus(self)
    }
  }

  implicit class canFireEvents[T <: Actor](val self: T) extends AnyVal {

    def click(): Actor = {
      val event1 = new InputEvent()
      event1.setType(InputEvent.Type.touchDown)
      self.fire(event1)

      val event2 = new InputEvent()
      event2.setType(InputEvent.Type.touchUp)
      self.fire(event2)

      self
    }
  }

  implicit class canFireAndReceiveEvents[T <: Actor](val self: T) extends AnyVal {

    def mapKeyDownToClick(vKey: Int, consume: Boolean = true): InputListener = {
      self.on({ case KeyDown(`vKey`) => self.click()}, consume = consume)
    }
  }

  implicit class canConsumeEvents[T <: Actor] (val self: T) extends AnyVal {
    def on[R](f: PartialFunction[GameEvent.InputEvent, R], consume: Boolean = true): InputListener = addAndReturnListener(keyListener(f, consume))

    def onClick(f: (Actor, Float, Float) => Unit): InputListener = addAndReturnListener(clickListener((x, y) => f(self, x,y)))
    def onClick(f: (Float, Float) => Unit): InputListener = onClick((_: Actor, x: Float, y: Float) => f(x,y))
    def onClick(f: (Actor => Unit)): InputListener = onClick((receiver: Actor, _: Float, _: Float) => f(receiver))
    def onClick(f: => Unit): InputListener = onClick((_: Float, _: Float) => f)

    def onKeyFocusChange(f: (T, Actor, Boolean) => Unit): FocusListener = addAndReturnListener(focusListener((emitter, newState) => f(self, emitter, newState)))
    def onKeyFocusChange(f: (T, Boolean) => Unit): FocusListener = addAndReturnListener(focusListener((_, newState) => f(self, newState)))
    def onKeyFocusChange(f: Boolean => Unit): FocusListener = addAndReturnListener(focusListener((_, newState) => f(newState)))
    def onKeyFocusGained(f: (T, Actor) => Unit): FocusListener = onKeyFocusChange((self, emitter, newState) => if (newState) f(self, emitter))
    def onKeyFocusGained(f: T => Unit): FocusListener = onKeyFocusGained({ (a, _) => f(a) }: (T, Actor) => Unit)
    def onKeyFocusGained(f: => Unit): FocusListener = onKeyFocusGained(_ => f)
    def onKeyFocusLost(f: (T, Actor) => Unit): FocusListener = onKeyFocusChange((self, emitter, newState) => if (!newState) f(self, emitter))
    def onKeyFocusLost(f: T => Unit): FocusListener = onKeyFocusLost({ (a, _) => f(a) }: (T, Actor) => Unit)
    def onKeyFocusLost(f: => Unit): FocusListener = onKeyFocusLost(_ => f)

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

  private def focusListener(f: (Actor, Boolean) => Unit): FocusListener = new FocusListener {
    override def keyboardFocusChanged(event: FocusEvent, emitter: Actor, focused: Boolean): Unit = {
      f(emitter, focused)
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
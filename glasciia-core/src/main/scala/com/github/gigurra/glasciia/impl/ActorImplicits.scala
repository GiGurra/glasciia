package com.github.gigurra.glasciia.impl

import com.badlogic.gdx.scenes.scene2d._
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener.FocusEvent
import com.badlogic.gdx.scenes.scene2d.utils.{ClickListener, FocusListener}
import com.github.gigurra.glasciia.GameEvent
import com.github.gigurra.glasciia.GameEvent.{CharTyped, KeyDown, KeyUp, KeyboardEvent}

trait CanFireEvents[-T] {
  def fire(t: T, event: InputEvent): Unit
}

object CanFireEvents {
  implicit val actorFirer: CanFireEvents[Actor] = new CanFireEvents[Actor] {
    override def fire(actor: Actor, event: InputEvent): Unit = actor.fire(event)
  }
}

trait CanAddEventListener[-T] {
  def addListener(t: T, l: EventListener): Unit
}

object CanAddEventListener {
  implicit val actorCanAddListener: CanAddEventListener[Actor] = new CanAddEventListener[Actor] {
    def addListener(t: Actor, l: EventListener): Unit = t.addListener(l)
  }
  implicit val stageCanAddListener: CanAddEventListener[Stage] = new CanAddEventListener[Stage] {
    def addListener(t: Stage, l: EventListener): Unit = t.addListener(l)
  }
}

/**
  * Created by johan on 2016-10-09.
  */
trait ActorImplicits {

  type Emitter = Actor

  implicit class canTakeKeyboardFocus[Subject <: Actor](self: Subject) {

    def setKeyFocus(): Unit = {
      self.getStage.setKeyboardFocus(self)
    }
  }

  implicit class canFireEvents[Shooter : CanFireEvents](self: Shooter) {

    def click(): Shooter = {
      val eventFirer = implicitly[CanFireEvents[Shooter]]

      val event1 = new InputEvent()
      event1.setType(InputEvent.Type.touchDown)
      eventFirer.fire(self, event1)

      val event2 = new InputEvent()
      event2.setType(InputEvent.Type.touchUp)
      eventFirer.fire(self, event2)

      self
    }
  }

  implicit class canFireAndReceiveEvents[Subject : CanFireEvents : CanAddEventListener](subject: Subject) {

    def mapKeyDownToClick(vKey: Int, consume: Boolean = true): InputListener = {
      subject.on({ case KeyDown(`vKey`) => subject.click()}, consume = consume)
    }
  }

  implicit class canConsumeEvents[Receiver : CanAddEventListener](self: Receiver) {
    def on[R](f: PartialFunction[GameEvent.InputEvent, R], consume: Boolean = true): InputListener = addAndReturnListener(keyListener(f, consume))

    def onClick(f: (Receiver, Float, Float) => Unit): InputListener = addAndReturnListener(clickListener((x, y) => f(self, x,y)))
    def onClick(f: (Float, Float) => Unit): InputListener = onClick((_: Receiver, x: Float, y: Float) => f(x,y))
    def onClick(f: (Receiver => Unit)): InputListener = onClick((receiver: Receiver, _: Float, _: Float) => f(receiver))
    def onClick(f: => Unit): InputListener = onClick((_: Float, _: Float) => f)

    def onKeyFocusChange(f: (Receiver, Emitter, Boolean) => Unit): FocusListener = addAndReturnListener(focusListener((emitter, newState) => f(self, emitter, newState)))
    def onKeyFocusChange(f: (Receiver, Boolean) => Unit): FocusListener = addAndReturnListener(focusListener((emitter, newState) => f(self, newState)))
    def onKeyFocusChange(f: Boolean => Unit): FocusListener = addAndReturnListener(focusListener((emitter, newState) => f(newState)))
    def onKeyFocusGained(f: (Receiver, Emitter) => Unit): FocusListener = onKeyFocusChange((self, emitter, newState) => if (newState) f(self, emitter))
    def onKeyFocusGained(f: Receiver => Unit): FocusListener = onKeyFocusGained({ (a, _) => f(a) }: (Receiver, Emitter) => Unit)
    def onKeyFocusGained(f: => Unit): FocusListener = onKeyFocusGained(_ => f)
    def onKeyFocusLost(f: (Receiver, Emitter) => Unit): FocusListener = onKeyFocusChange((self, emitter, newState) => if (!newState) f(self, emitter))
    def onKeyFocusLost(f: Receiver => Unit): FocusListener = onKeyFocusLost({ (a, _) => f(a) }: (Receiver, Emitter) => Unit)
    def onKeyFocusLost(f: => Unit): FocusListener = onKeyFocusLost(_ => f)

    def blockInputEventPropagation(): InputListener = addAndReturnListener(new InputListener{
      override def handle(e: Event): Boolean = {
        super.handle(e)
        true
      }
    })

    private def addAndReturnListener[T <: EventListener](listener: T): T ={
      implicitly[CanAddEventListener[Receiver]].addListener(self, listener)
      listener
    }
  }

  private def focusListener(f: (Emitter, Boolean) => Unit): FocusListener = new FocusListener {
    override def keyboardFocusChanged(event: FocusEvent, emitter: Emitter, focused: Boolean): Unit = {
      f(emitter, focused)
    }
  }

  private def clickListener(f: (Float, Float) => Unit): InputListener = new ClickListener {
    override def clicked(event: InputEvent, x: Float, y: Float): Unit = f(x, y)
  }

  private def keyListener[R](f: PartialFunction[KeyboardEvent, R], consume: Boolean): InputListener = new InputListener {
    override def keyDown(event: InputEvent, keycode: Int): Boolean = tryConsume(f, KeyDown(keycode)) && consume
    override def keyUp(event: InputEvent, keycode: Int): Boolean = tryConsume(f, KeyUp(keycode)) && consume
    override def keyTyped(event: InputEvent, character: Char): Boolean = tryConsume(f, CharTyped(character)) && consume
  }

  private def tryConsume[Event, R](f: PartialFunction[Event, R], event: Event): Boolean = {
    f.lift.apply(event) match {
      case Some(_) => true
      case None => false
    }
  }
}

object ActorImplicits extends ActorImplicits

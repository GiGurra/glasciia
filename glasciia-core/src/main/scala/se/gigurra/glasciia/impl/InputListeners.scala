package se.gigurra.glasciia.impl
import com.badlogic.gdx.scenes.scene2d.utils.{ClickListener, FocusListener}
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener.FocusEvent
import com.badlogic.gdx.scenes.scene2d._
import se.gigurra.glasciia.AppEvent
import se.gigurra.glasciia.AppEvent.{CharTyped, KeyDown, KeyUp, KeyboardEvent}

import scala.language.{implicitConversions, reflectiveCalls}


/**
  * Created by johan on 2016-10-09.
  */
trait InputListeners {

  type CanAddListener = { def addListener(l: EventListener): Boolean }
  type CanFireEvents = { def fire (event: Event): Boolean }
  type CanFireAndReceiveEvents = CanAddListener with CanFireEvents
  type Emitter = Actor

  implicit class canTakeKeyboardFocus[Subject <: Actor](self: Subject) {

    def setKeyFocus(): Subject = {
      self.getStage.setKeyboardFocus(self)
      self
    }
  }

  implicit class canFireEvents[Shooter <: CanFireEvents](self: Shooter) {

    def click(): Shooter = {
      val event1 = new InputEvent()
      event1.setType(InputEvent.Type.touchDown)
      self.fire(event1)

      val event2 = new InputEvent()
      event2.setType(InputEvent.Type.touchUp)
      self.fire(event2)

      self
    }
  }

  implicit class canFireAndReceiveEvents[Subject <: CanFireAndReceiveEvents](subject: Subject) {

    def mapKeyDownToClick(vKey: Int, consume: Boolean = true): Subject = {
      subject.on({ case KeyDown(`vKey`) => subject.click()}, consume = consume)
      subject
    }
  }

  implicit class canConsumeEvents[Receiver <: CanAddListener](self: Receiver) {
    def on[R](f: PartialFunction[AppEvent.InputEvent, R], consume: Boolean = true): InputListener = addAndReturnListener(keyListener(f, consume))

    def onClick[R](f: (Receiver, Float, Float) => R): InputListener = addAndReturnListener(clickListener((x, y) => f(self, x,y)))
    def onClick[R](f: (Float, Float) => R): InputListener = onClick((_: Receiver, x: Float, y: Float) => f(x,y))
    def onClick[R](f: (Receiver => R)): InputListener = onClick((receiver: Receiver, _: Float, _: Float) => f(receiver))
    def onClick[R](f: => R): InputListener = onClick((_: Float, _: Float) => f)

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
      self.addListener(listener)
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

object InputListeners extends InputListeners

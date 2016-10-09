package se.gigurra.glasciia.impl
import com.badlogic.gdx.scenes.scene2d.utils.{ClickListener, FocusListener}
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener.FocusEvent
import com.badlogic.gdx.scenes.scene2d.{Actor, EventListener, InputEvent, InputListener}
import se.gigurra.glasciia.AppEvent.{CharTyped, KeyDown, KeyUp}

import scala.language.{implicitConversions, reflectiveCalls}


/**
  * Created by johan on 2016-10-09.
  */
trait InputListeners {

  type CanAddListener = { def addListener(l: EventListener): Boolean }
  type Emitter = Actor

  implicit class canConsumeEvents[Receiver <: CanAddListener](self: Receiver) {
    def onKeyDown(f: PartialFunction[KeyDown, Unit], consume: Boolean = true): InputListener = addAndReturnListener(keyDown(f, consume))
    def onKeyUp(f: PartialFunction[KeyUp, Unit], consume: Boolean = true): InputListener = addAndReturnListener(keyUp(f, consume))
    def onCharTyped(f: PartialFunction[CharTyped, Unit], consume: Boolean = true): InputListener = addAndReturnListener(charTyped(f, consume))

    def onClicked(f: (Receiver, Float, Float) => Unit): InputListener = addAndReturnListener(click((x,y) => f(self, x,y)))
    def onClicked(f: (Float, Float) => Unit): InputListener = onClicked((_, x, y) => f(x,y))
    def onClicked(f: => Unit): InputListener = onClicked((_,_) => f)

    def onKeyFocusChange(f: (Receiver, Emitter, Boolean) => Unit): FocusListener = addAndReturnListener(focus((emitter, newState) => f(self, emitter, newState)))
    def onKeyFocusChange(f: (Receiver, Boolean) => Unit): FocusListener = addAndReturnListener(focus((emitter, newState) => f(self, newState)))
    def onKeyFocusChange(f: Boolean => Unit): FocusListener = addAndReturnListener(focus((emitter, newState) => f(newState)))
    def onKeyFocusGained(f: (Receiver, Emitter) => Unit): FocusListener = onKeyFocusChange((self, emitter, newState) => if (newState) f(self, emitter))
    def onKeyFocusGained(f: Receiver => Unit): FocusListener = onKeyFocusGained({ (a, _) => f(a) }: (Receiver, Emitter) => Unit)
    def onKeyFocusGained(f: => Unit): FocusListener = onKeyFocusGained(_ => f)
    def onKeyFocusLost(f: (Receiver, Emitter) => Unit): FocusListener = onKeyFocusChange((self, emitter, newState) => if (!newState) f(self, emitter))
    def onKeyFocusLost(f: Receiver => Unit): FocusListener = onKeyFocusLost({ (a, _) => f(a) }: (Receiver, Emitter) => Unit)
    def onKeyFocusLost(f: => Unit): FocusListener = onKeyFocusLost(_ => f)

    private def addAndReturnListener[T <: EventListener](listener: T): T ={
      self.addListener(listener)
      listener
    }
  }

  def focus(f: (Emitter, Boolean) => Unit): FocusListener = new FocusListener {
    override def keyboardFocusChanged(event: FocusEvent, emitter: Emitter, focused: Boolean): Unit = {
      f(emitter, focused)
    }
  }

  def click(f: (Float, Float) => Unit): InputListener = new ClickListener {
    override def clicked(event: InputEvent, x: Float, y: Float): Unit = f(x, y)
  }

  def keyDown(f: PartialFunction[KeyDown, Unit], consume: Boolean): InputListener = new InputListener {
    override def keyDown(event: InputEvent, keycode: Int): Boolean = tryConsume(f, KeyDown(keycode)) && consume
  }

  def keyUp(f: PartialFunction[KeyUp, Unit], consume: Boolean): InputListener = new InputListener {
    override def keyUp(event: InputEvent, keycode: Int): Boolean = tryConsume(f, KeyUp(keycode)) && consume
  }

  def charTyped(f: PartialFunction[CharTyped, Unit], consume: Boolean): InputListener = new InputListener {
    override def keyTyped(event: InputEvent, character: Char): Boolean = tryConsume(f, CharTyped(character)) && consume
  }

  private def tryConsume[Event](f: PartialFunction[Event, Unit], event: Event): Boolean = {
    f.lift.apply(event) match {
      case Some(_) => true
      case None => false
    }
  }
}

object InputListeners extends InputListeners

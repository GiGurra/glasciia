package se.gigurra.glasciia.impl

import java.util.concurrent.ConcurrentLinkedQueue

import com.badlogic.gdx.{ApplicationListener, InputProcessor}
import rx.lang.scala.{Observable, Subject}
import se.gigurra.glasciia.{App, ApplicationEvent, Canvas}
import se.gigurra.glasciia.ApplicationEvent._
import se.gigurra.math.Vec2

/**
  * Created by johan on 2016-10-01.
  */
trait ApplicationEventListener { self: App =>

  private val subject = Subject[ApplicationEvent]().toSerialized
  subject.hasObservers

  def events: Observable[ApplicationEvent] = subject

  def handleEvents(f: ApplicationEvent => Unit, crashHandler: Throwable => Unit = App.defaultCrashHandler): Unit = {
    events.foreach(f, crashHandler)
  }

  private var canvas: Canvas = _
  private var initReceived: Boolean = false

  protected val inputListener = new InputProcessor {
    override def keyTyped(character: Char): Boolean = consume(CharTyped(character))
    override def keyDown(keycode: Int): Boolean = consume(KeyDown(keycode))
    override def keyUp(keycode: Int): Boolean = consume(KeyUp(keycode))
    override def mouseMoved(screenX: Int, screenY: Int): Boolean = consume(MouseMove(Vec2(screenX, screenY)))
    override def scrolled(amount: Int): Boolean = consume(MouseScrolled(amount))
    override def touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = consume(TouchDown(Vec2(screenX, screenY), pointer, button))
    override def touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = consume(TouchUp(Vec2(screenX, screenY), pointer, button))
    override def touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean = consume(TouchDrag(Vec2(screenX, screenY), pointer))
  }

  protected val appListener = new ApplicationListener {
    override def resize(width: Int, height: Int): Unit = consume(Resize(Vec2(width, height), canvas: Canvas))
    override def dispose(): Unit = consume(Exit(canvas: Canvas))
    override def pause(): Unit = consume(Pause(canvas: Canvas))
    override def render(): Unit = consume(Render(canvas))
    override def resume(): Unit = consume(Resume(canvas: Canvas))
    override def create(): Unit = {
      canvas = Canvas(self)
      flushQueuedOps()
      setCreated()
      consume(Init(canvas))
    }
  }
  protected def setCreated(): Unit

  private def consume(ev: ApplicationEvent): Boolean = {
    flushQueuedOps()
    ev match { // Guarantee we always receieve an init event!
      case init: Init =>
        if (subject.hasObservers) {
          initReceived = true
          subject.onNext(init)
        } else {
          queuedOps.add(() => consume(init))
        }
      case _ if initReceived => subject.onNext(ev)
      case _ => // Drop this event. Init must be consumed first
    }
    true
  }

  private val queuedOps = new ConcurrentLinkedQueue[() => Unit]()

  protected def executeOnRenderThread(op: => Unit): Unit = {
    if (isOnRenderThread) {
      op
    } else {
      queuedOps.add(() => op)
    }
  }

  private def flushQueuedOps(): Unit = {
    while(!queuedOps.isEmpty) {
      queuedOps.poll().apply()
    }
  }

}

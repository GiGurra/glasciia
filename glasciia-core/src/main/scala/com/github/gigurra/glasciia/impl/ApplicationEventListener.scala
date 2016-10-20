package com.github.gigurra.glasciia.impl

import java.util.concurrent.ConcurrentLinkedQueue

import com.badlogic.gdx.{ApplicationListener, Gdx, InputProcessor}
import com.github.gigurra.glasciia.GameEvent._
import com.github.gigurra.glasciia.{Game, GameEvent, Canvas}
import com.github.gigurra.math.Vec2
import rx.lang.scala.{Observable, Subject}

/**
  * Created by johan on 2016-10-01.
  */
trait ApplicationEventListener extends ApplicationListener { self: Game =>

  def handleEvents(f: PartialFunction[GameEvent, Unit], crashHandler: Throwable => Unit = Game.defaultCrashHandler): Unit = {
    subject.foreach(f.applyOrElse(_, (_: Any) => ()), crashHandler)
  }

  def canvas: Canvas = _canvas

    /////////////////////////////////////////////
  // Implemented expectations

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

  protected lazy val appListener = {
    handleEvents(self.eventHandler, self.crashHandler)
    new ApplicationListener {
      override def resize(width: Int, height: Int): Unit = consume(Resize(Vec2(width, height), canvas: Canvas))
      override def dispose(): Unit = consume(Exit(canvas: Canvas))
      override def pause(): Unit = consume(Pause(canvas: Canvas))
      override def render(): Unit = {
        canvas.setDrawTime()
        consume(Render(canvas))
      }
      override def resume(): Unit = consume(Resume(canvas: Canvas))
      override def create(): Unit = {
        _canvas = Canvas(self)
        Gdx.input.setInputProcessor(inputListener)
        flushQueuedOps()
        consume(Init(canvas))
      }
    }
  }

  override def resize(width: Int, height: Int): Unit = appListener.resize(width, height)
  override def dispose(): Unit = appListener.dispose()
  override def pause(): Unit = appListener.pause()
  override def render(): Unit = appListener.render()
  override def resume(): Unit = appListener.resume()
  override def create(): Unit = appListener.create()


  /////////////////////////////////////////////
  // Private

  private def consume(ev: GameEvent): Boolean = {
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

  private def flushQueuedOps(): Unit = {
    while(!queuedOps.isEmpty) {
      queuedOps.poll().apply()
    }
  }

  private var _canvas: Canvas = null : Canvas
  private val subject = Subject[GameEvent]().toSerialized
  private var initReceived: Boolean = false

}

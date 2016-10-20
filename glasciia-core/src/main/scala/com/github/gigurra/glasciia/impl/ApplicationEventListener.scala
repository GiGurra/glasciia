package com.github.gigurra.glasciia.impl

import com.badlogic.gdx.{ApplicationListener, Gdx, InputProcessor}
import com.github.gigurra.glasciia.GameEvent._
import com.github.gigurra.glasciia.{Game, GameEvent, Canvas}
import com.github.gigurra.math.Vec2

/**
  * Created by johan on 2016-10-01.
  */
trait ApplicationEventListener extends ApplicationListener { self: Game =>

  def canvas: Canvas = _canvas

    /////////////////////////////////////////////
  // Implemented expectations

  protected lazy val appListener = {
    def consume(ev: GameEvent): Boolean = {
      self.eventHandler.lift.apply(ev) match {
        case Some(_) => true
        case None => false
      }
    }

    val inputListener = new InputProcessor {
      override def keyTyped(character: Char): Boolean = consume(CharTyped(character))
      override def keyDown(keycode: Int): Boolean = consume(KeyDown(keycode))
      override def keyUp(keycode: Int): Boolean = consume(KeyUp(keycode))
      override def mouseMoved(screenX: Int, screenY: Int): Boolean = consume(MouseMove(Vec2(screenX, screenY)))
      override def scrolled(amount: Int): Boolean = consume(MouseScrolled(amount))
      override def touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = consume(TouchDown(Vec2(screenX, screenY), pointer, button))
      override def touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = consume(TouchUp(Vec2(screenX, screenY), pointer, button))
      override def touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean = consume(TouchDrag(Vec2(screenX, screenY), pointer))
    }

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

  private var _canvas: Canvas = null : Canvas

}

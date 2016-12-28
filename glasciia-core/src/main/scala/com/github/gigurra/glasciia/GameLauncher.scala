package com.github.gigurra.glasciia

import com.badlogic.gdx.{ApplicationListener, Gdx, InputProcessor}
import com.github.gigurra.glasciia.GameEvent._
import com.github.gigurra.math.Vec2
import scala.language.implicitConversions

/**
  * Created by johan on 2016-10-30.
  * Helper class to not have to wait for an Init before creating OpenGL/GDX resources/objects
  */
class GameLauncher(impl: GameLauncherIfc) extends ApplicationListener with InputProcessor {

  private var game: Game = null.asInstanceOf[Game]

  override def create(): Unit = {
    game = impl.launch()
    Gdx.input.setInputProcessor(this)
    game.consume(Init(game.time, game.canvas))
  }

  override def resize(width: Int, height: Int): Unit = {
    game.consume(Resize(game.time, game.canvas, Vec2(width, height)))
  }

  override def dispose(): Unit = {
    game.consume(Exit(game.time, game.canvas))
  }

  override def pause(): Unit = {
    game.consume(Pause(game.time, game.canvas))
  }

  override def render(): Unit = {
    game.canvas.setDrawTime()
    game.consume(Render(game.time, game.canvas))
  }

  override def resume(): Unit = {
    game.consume(Resume(game.time, game.canvas))
  }

  override def keyTyped(character: Char): Boolean = {
    game.consume(CharTyped(character))
  }

  override def keyDown(keycode: Int): Boolean = {
    game.consume(KeyDown(keycode))
  }

  override def keyUp(keycode: Int): Boolean = {
    game.consume(KeyUp(keycode))
  }

  override def mouseMoved(screenX: Int, screenY: Int): Boolean = {
    game.consume(MouseMove(Vec2(screenX, screenY)))
  }

  override def scrolled(amount: Int): Boolean = {
    game.consume(MouseScrolled(amount))
  }

  override def touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = {
    val out = game.consume(TouchDown(Vec2(screenX, screenY), pointer, button))
    impl.onTouchDown()
    out
  }

  override def touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = {
    val out = game.consume(TouchUp(Vec2(screenX, screenY), pointer, button))
    impl.onTouchUp(game.canvas.isTouched)
    out
  }

  override def touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean = {
    val out = game.consume(TouchDrag(Vec2(screenX, screenY), pointer))
    impl.onTouchDrag()
    out
  }

}

object GameLauncher {
  def apply(launchFcn: => Game): GameLauncher = apply(new GameLauncherAdapter { override def launch(): Game = launchFcn })
  def apply(javaIfc: GameLauncherIfc): GameLauncher = new GameLauncher(javaIfc)
  implicit def game2Launcher(f: => Game): GameLauncher = apply(f)
}

trait GameLauncherIfc {

  /**
    * Used for lazy creation of the Game object. Will be called from the GL thread only
    */
  def launch(): Game

  /**
    * Used to solve IOS issues with slow input rates
    * - IOS 10 requires the app to call GLKView.display after every touchdrag event
    * and have it in paused mode between a touchDown and touchUp
    */
  def onTouchDown(): Unit

  /**
    * Used to solve IOS issues with slow input rates
    * - IOS 10 requires the app to call GLKView.display after every touchdrag event
    * and have it in paused mode between a touchDown and touchUp
    */
  def onTouchDrag(): Unit

  /**
    * Used to solve IOS issues with slow input rates
    * - IOS 10 requires the app to call GLKView.display after every touchdrag event
    * and have it in paused mode between a touchDown and touchUp
    */
  def onTouchUp(stillTouched: Boolean): Unit
}

abstract class GameLauncherAdapter extends GameLauncherIfc {
  override def onTouchDown(): Unit = {}
  override def onTouchDrag(): Unit = {}
  override def onTouchUp(stillTouched: Boolean): Unit = {}
}

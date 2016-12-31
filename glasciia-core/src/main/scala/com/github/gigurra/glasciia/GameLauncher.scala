package com.github.gigurra.glasciia

import com.badlogic.gdx.{ApplicationListener, Gdx, InputProcessor}
import com.github.gigurra.glasciia.GameEvent._
import com.github.gigurra.math.Vec2
import scala.language.implicitConversions

/**
  * Created by johan on 2016-10-30.
  * Helper class to not have to wait for an Init before creating OpenGL/GDX resources/objects
  */
class GameLauncher[R <: Resources](impl: GameLauncherIfc[R])
  extends ApplicationListener
    with InputProcessor
    with Logging {

  private var game: Game = null.asInstanceOf[Game]
  private var loadingScreen: Game = null.asInstanceOf[Game]
  private var resources: R = null.asInstanceOf[R]
  private var firstFrame: Boolean = true

  override def create(): Unit = {
    log.info("Loading..")
    loadingScreen = impl.loadingScreen()
    game = loadingScreen
    resources = impl.resources()
    Gdx.input.setInputProcessor(this)
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
    if (!firstFrame) {
      checkFinishedLoading()
    }
    game.canvas.setDrawTime()
    game.consume(Render(game.time, game.canvas))
    firstFrame = false
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
    game.consume(TouchDown(Vec2(screenX, screenY), pointer, button))
  }

  override def touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = {
    game.consume(TouchUp(Vec2(screenX, screenY), pointer, button))
  }

  override def touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean = {
    game.consume(TouchDrag(Vec2(screenX, screenY), pointer))
  }

  private def checkFinishedLoading(): Unit = {

    if (!resources.finished) {
      resources.load(impl.loadingScreenFrameTime())
    }

    if (resources.finished && (game eq loadingScreen)) {
      log.info("Loading done - launching game!")
      game = impl.launch(resources)
    }
  }
}

object GameLauncher {
  def apply[R <: Resources](javaIfc: GameLauncherIfc[R]): GameLauncher[R] = new GameLauncher[R](javaIfc)
}

abstract class GameLauncherIfc[R <: Resources] {
  def resources(): R
  def loadingScreen(): Game
  def loadingScreenFrameTime(): Long = 100
  def launch(resources: R): Game
}

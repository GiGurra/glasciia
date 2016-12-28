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
    game.consume(TouchDown(Vec2(screenX, screenY), pointer, button))
  }

  override def touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = {
    game.consume(TouchUp(Vec2(screenX, screenY), pointer, button))
  }

  override def touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean = {
    game.consume(TouchDrag(Vec2(screenX, screenY), pointer))
  }

}

object GameLauncher {
  def apply(launchFcn: => Game): GameLauncher = apply(new GameLauncherIfc { override def launch(): Game = launchFcn })
  def apply(javaIfc: GameLauncherIfc): GameLauncher = new GameLauncher(javaIfc)
  implicit def game2Launcher(f: => Game): GameLauncher = apply(f)
}

trait GameLauncherIfc {
  def launch(): Game
}

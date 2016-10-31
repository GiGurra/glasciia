package com.github.gigurra.glasciia

import com.badlogic.gdx.{ApplicationListener, Gdx, InputProcessor}
import com.github.gigurra.glasciia.GameEvent._
import com.github.gigurra.math.Vec2
import scala.language.implicitConversions

/**
  * Created by johan on 2016-10-30.
  * Helper class to not have to wait for an Init before creating OpenGL/GDX resources/objects
  */
class GameLauncher(launchFcn: => Game) extends ApplicationListener {

  private lazy val game = launchFcn
  import game._

  override def create(): Unit = {
    Gdx.input.setInputProcessor(new InputProcessor {
      override def keyTyped(character: Char): Boolean = consume(CharTyped(character))
      override def keyDown(keycode: Int): Boolean = consume(KeyDown(keycode))
      override def keyUp(keycode: Int): Boolean = consume(KeyUp(keycode))
      override def mouseMoved(screenX: Int, screenY: Int): Boolean = consume(MouseMove(Vec2(screenX, screenY)))
      override def scrolled(amount: Int): Boolean = consume(MouseScrolled(amount))
      override def touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = consume(TouchDown(Vec2(screenX, screenY), pointer, button))
      override def touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = consume(TouchUp(Vec2(screenX, screenY), pointer, button))
      override def touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean = consume(TouchDrag(Vec2(screenX, screenY), pointer))
    })
    consume(Init(time))
  }

  override def resize(width: Int, height: Int): Unit = {
    consume(Resize(time, Vec2(width, height), canvas))
  }

  override def dispose(): Unit = {
    consume(Exit(time))
  }

  override def pause(): Unit = {
    consume(Pause(time))
  }

  override def render(): Unit = {
    canvas.setDrawTime()
    consume(Render(time))
  }

  override def resume(): Unit = {
    consume(Resume(time))
  }
}

object GameLauncher {
  def apply(launchFcn: => Game): GameLauncher = new GameLauncher(launchFcn)
  def apply(javaIfc: GameLauncherIfc): GameLauncher = apply(javaIfc.launch())
  implicit def game2Launcher(f: => Game): GameLauncher = new GameLauncher(f)
}

trait GameLauncherIfc {
  def launch(): Game
}

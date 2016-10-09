package se.gigurra.glasciia.test1

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.scenes.scene2d.Stage
import se.gigurra.glasciia.AppEvent._
import se.gigurra.glasciia.Glasciia._
import se.gigurra.glasciia._
import se.gigurra.glasciia.impl._
import se.gigurra.glasciia.test1.testcomponents._

/**
  * Created by johan on 2016-09-26.
  */
object Main {

  def main(args: Array[String]): Unit = {

    val app = new App(Conf.initialWindow, Conf.initialGl) with LwjglImplementation

    app.handleEvents {

      case Init(canvas) =>
        loadResources(app)

        printShaders(canvas.batch)

      case Render(canvas) =>
        updateCameraPos(canvas)
        drawWorld(canvas)
        drawGameGui(canvas)
        drawMenu(canvas)

      case input: InputEvent =>
        val mainMenu = app.resource[Stage]("gui:main-menu")
        val gameGui = app.resource[Stage]("gui:game-world")
        val controlsInverted = app.getResource[Boolean]("controls-inverted").getOrElse(false)
        val gameKeyState = app.resource[Keyboard]("world-input-keyboard")

        val invertedControls: PartialFunction[InputEvent, KeyboardEvent] = {
          case KeyDown(Keys.DOWN) => KeyDown(Keys.UP)
          case KeyDown(Keys.UP) => KeyDown(Keys.DOWN)
          case KeyDown(Keys.LEFT) => KeyDown(Keys.RIGHT)
          case KeyDown(Keys.RIGHT) => KeyDown(Keys.LEFT)
          case KeyUp(Keys.DOWN) => KeyUp(Keys.UP)
          case KeyUp(Keys.UP) => KeyUp(Keys.DOWN)
          case KeyUp(Keys.LEFT) => KeyUp(Keys.RIGHT)
          case KeyUp(Keys.RIGHT) => KeyUp(Keys.LEFT)
        }

        input
          .mapIf(controlsInverted, invertedControls)
          .filter(gameKeyState.releaseHook)
          .filter(mainMenu)
          .filter(gameGui)
          .filter(gameKeyState)
          .filter {
            case MouseScrolled(amount) =>
              app.canvas.camera.zoom += amount * 0.1f
              //println(s"MouseEvent propagated to world/Not consumed by gui: $event")
            case KeyDown(Keys.ESCAPE) =>
              mainMenu.show()
            case event: KeyboardEvent =>
              println(s"KeyboardEvent propagated to world/Not consumed by gui: $event")
          }
    }

  }
}

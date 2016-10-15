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
        setInitValues(app)
        printShaders(canvas.batch)
        app.reloadTexturesAfterContextLoss() // TODO: For testing only! Don't have here in production code! Intentional GPU memory leak!

      case Render(canvas) =>
        updateWorld(canvas)
        drawWorld(canvas)
        drawGameGui(canvas)
        drawMenu(canvas)

      case Resume(canvas) =>
        app.reloadTexturesAfterContextLoss()

      case input: InputEvent =>
        val mainMenu = app.resource[Stage]("gui:main-menu")
        val gameGui = app.resource[Stage]("gui:game-world")
        val controlsInverted = app.getResource[Boolean]("controls-inverted").getOrElse(false)

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
          .filter(mainMenu)
          .filter(gameGui)
          .filter {
            case MouseScrolled(amount) =>
              canvas.setZoom(
                newValue = canvas.zoom + amount * 0.1f,
                preserveCursorWorldPosition = true, // Supreme commander style!
                projectionArea = canvas.wholeCanvasProjectionArea
              )
            case KeyDown(Keys.ESCAPE) => mainMenu.show()
            case event: KeyboardEvent => println(s"KeyboardEvent propagated to world/Not consumed by gui: $event")
          }
    }

    def canvas: Canvas = app.canvas
  }
}

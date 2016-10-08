package se.gigurra.glasciia.test1

import com.badlogic.gdx.graphics.Cursor
import com.badlogic.gdx.scenes.scene2d.Stage
import se.gigurra.glasciia.AppEvent._
import se.gigurra.glasciia.Glasciia._
import se.gigurra.glasciia._
import se.gigurra.glasciia.impl._
import se.gigurra.glasciia.test1.testcomponents.{drawWorld, loadResources, printShaders, updateCameraPos}

/**
  * Created by johan on 2016-09-26.
  */
object Main {

  def main(args: Array[String]): Unit = {

    val app = new App(Conf.initialWindow, Conf.initialGl) with LwjglImplementation

    loadResources(app)

    app.handleEvents {

      case Init(canvas) =>
        printShaders(canvas.batch)
        canvas.setCursor(app.resource[Cursor]("cool-cursor"))

      case Render(canvas) =>
        updateCameraPos(canvas)
        drawWorld(canvas)

      case input: InputEvent =>

        val mainMenuGui = app.resource[Stage]("gui:main-menu")
        val mainMenuVisible = app.resource[Boolean]("gui:main-menu:visible")

        input
          .filterIf(mainMenuVisible, mainMenuGui)
          .filter {
            case event: KeyboardEvent =>
              println(s"Input event propageted to world/Not consumed by gui: $event")
          }
    }

  }
}

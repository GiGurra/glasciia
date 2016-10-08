package se.gigurra.glasciia.test1

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Cursor
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

    loadResources(app)

    app.handleEvents {

      case Init(canvas) =>
        printShaders(canvas.batch)
        canvas.setCursor(app.resource[Cursor]("cool-cursor"))

      case Render(canvas) =>
        updateCameraPos(canvas)
        drawWorld(canvas)
        drawGui(canvas, mainMenu, Gdx.graphics.getDeltaTime)

      case input: InputEvent =>


        input
          .filter(mainMenu)
          .filter {
            case event: MouseEvent =>
              println(s"MouseEvent propagated to world/Not consumed by gui: $event")
            case event: KeyboardEvent =>
              println(s"KeyboardEvent propagated to world/Not consumed by gui: $event")
          }
    }


    def mainMenu: Gui = app.resource[Gui]("gui:main-menu")

  }
}

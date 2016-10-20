package com.github.gigurra.glasciia.test1

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.backends.lwjgl.{LwjglApplication, LwjglApplicationConfiguration}
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.gigurra.glasciia.GameEvent._
import com.github.gigurra.glasciia.Glasciia._
import com.github.gigurra.glasciia._
import com.github.gigurra.glasciia.test1.testcomponents._

/**
  * Created by johan on 2016-09-26.
  */
object Main {

  def main(args: Array[String]): Unit = {

    val config = new LwjglApplicationConfiguration {
      x = 100
      y = 100
      width = 640
      height = 480
      resizable = true
      fullscreen = false
      title = "Test Game 1"
      forceExit = false
      vSyncEnabled = true
      samples = 4
      foregroundFPS = 0 // 0 means no limit
      backgroundFPS = 30
    }

    new LwjglApplication(new Game {
      def eventHandler = {
        case Init(canvas) =>
          loadResources(this)
          setInitValues(this)
          printShaders(canvas.batch)
          canvas.game.reloadTexturesAfterContextLoss() // TODO: For testing only! Don't have here in production code! Intentional GPU memory leak!

        case Render(canvas) =>
          updateWorld(canvas)
          drawWorld(canvas)
          drawGameGui(canvas)
          drawMenu(canvas)

        case Resume(canvas) =>
          canvas.game.reloadTexturesAfterContextLoss()

        case input: InputEvent =>
          val mainMenu = resource[Stage]("gui:main-menu")
          val gameGui = resource[Stage]("gui:game-world")
          val controlsInverted = resource[Boolean]("controls-inverted", default = false)

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
                  preserveMouseWorldPosition = true, // Supreme commander style!
                  projectionArea = canvas.wholeCanvasProjectionArea
                )
              case KeyDown(Keys.ESCAPE) => mainMenu.show()
              case event: KeyboardEvent => println(s"KeyboardEvent propagated to world/Not consumed by gui: $event")
            }
      }
    }, config)

  }
}

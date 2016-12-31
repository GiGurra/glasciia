package com.github.gigurra.glasciia.test1

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.gigurra.glasciia._
import com.github.gigurra.glasciia.Glasciia._
import com.github.gigurra.glasciia.GameEvent._
import com.github.gigurra.glasciia.test1.testcomponents._

/**
  * Created by johan on 2016-10-31.
  */
class TestGame extends Game with Logging {

  implicit val gestureState = GestureState()
  val resources = loadResources(this)
  resources.reloadAfterContextLoss() // TODO: For testing only! Don't have here in production code! Intentional GPU memory leak!
  setInitValues(this)
  printShaders(canvas.batch)

  val testTimedAct = new Act(Vector(
    new TimedScene(length = 1000) { override def onEnd(): Unit = { log.info("Scene1 ended")} },
    new TimedScene(length = 1000) { override def onEnd(): Unit = { log.info("Scene2 ended")} },
    new TimedScene(length = 1000) { override def onEnd(): Unit = { log.info("Scene3 ended")} },
    new TimedScene(length = 1000) { override def onEnd(): Unit = { log.info("Scene4 ended")} }
  )) {
    override def onEnd(): Unit = log.info("Act ended")
  }

  def eventHandler = {

    case Render(time, _) =>
      updateWorld(canvas, resources)
      drawWorld(canvas, resources)
      drawGameGui(canvas, resources)
      drawMenu(canvas, resources)

      testTimedAct.update(time)

    case input: InputEvent =>
      val mainMenu = resources[Stage]("gui:main-menu")
      val gameGui = resources[Stage]("gui:game-world")
      val controlsInverted = resources[Boolean]("controls-inverted", default = false)

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
        .filter(testTimedAct)
        .filter(gameGui)
        .filterGestures {
          case GesturePan(pos, delta) => log.info(s"Panning from $pos with amount $delta")
          case GestureFling(velocity, button) => log.info(s"Flinging with velocity $velocity using button $button")
        }
        .filter {
          case MouseScrolled(amount) =>
            canvas.setZoom(
              newValue = canvas.zoom + amount * 0.1f,
              preserveMouseWorldPosition = true, // Supreme commander style!
              projectionArea = canvas.wholeCanvasProjectionArea
            )
          case KeyDown(Keys.ESCAPE) => mainMenu.show()
          case event: KeyboardEvent => log.info(s"KeyboardEvent propagated to world/Not consumed by gui: $event")
        }
  }
}

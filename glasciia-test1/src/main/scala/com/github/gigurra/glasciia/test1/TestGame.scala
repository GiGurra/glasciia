package com.github.gigurra.glasciia.test1

import com.badlogic.gdx.Input.Keys
import com.github.gigurra.glasciia._
import com.github.gigurra.glasciia.Glasciia._
import com.github.gigurra.glasciia.GameEvent._
import com.github.gigurra.glasciia.Scale.{Constant, LinearShortestSide}
import com.github.gigurra.glasciia.test1.testcomponents._
import com.github.gigurra.math.Vec2

/**
  * Created by johan on 2016-10-31.
  */
class TestGame(resources: TestGameResources) extends Game with Logging {

  implicit val gestureState = GestureState()
  setInitValues(this)
  printShaders(canvas.batch)

  val transitions = TransitionSystem()

  for {
    _ <- transitions.execute(Delay(1000))
    _ = println("Transition1 ended")
    _ <- transitions.execute(Delay(1000))
    _ = println("Transition2 ended")
    _ <- transitions.execute(Delay(1000))
    _ = println("Transition3 ended")
  } ()

  val testTimedAct = new Act(Vector(
    new TimedScene(length = 1000) { override def onEnd(): Unit = { log.info("Scene1 ended")} },
    new TimedScene(length = 1000) { override def onEnd(): Unit = { log.info("Scene2 ended")} },
    new TimedScene(length = 1000) { override def onEnd(): Unit = { log.info("Scene3 ended")} },
    new TimedScene(length = 1000) { override def onEnd(): Unit = { log.info("Scene4 ended")} }
  )) {
    override def onEnd(): Unit = log.info("Act ended")
  }

  private val pda = new PDAScreen[GameScreen[TestGame]]()
  private val guiSystem = GuiSystem(
    "main-menu" -> resources[MainMenu]("gui:main-menu"),
    "game-gui" -> resources[GameWorldGui]("gui:game-world")
  )

  resources[MainMenu]("gui:main-menu").startSignal.connect { _ =>
    pda.pop()
    guiSystem.transition(
      to = "game-gui",
      transitionTime = 1000L,
      transition = new RotateGuiTransition(
        ccw = false,
        angleRange = 180.0f,
        relativeLocation = Vec2(0.5f, 1.0f)
      )
    )
  }
  
  private val ingame = new GameScreen[TestGame](this) {
    override def eventHandler: PartialFunction[GameEvent, Unit] = {

      case Render(time, _) =>
        updateWorld(canvas, resources)
        drawWorld(canvas, resources)
        if (pda.isTopMost(this))
          guiSystem.draw(canvas, screenFitting = LinearShortestSide(reference = Vec2(640, 480)) * Constant(0.75f))
        transitions.update(time)
        testTimedAct.update(time)

      case input: InputEvent =>
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
          .filter(testTimedAct)
          .filter(guiSystem)
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
            case KeyDown(Keys.ESCAPE) =>
              pda.push(mainMenu)
              guiSystem.transition(
                to = "main-menu",
                transitionTime = 1000L,
                transition = new SwipeGuiTransition(direction = -Vec2(1.0f, 0.0f))
              )
            case event: KeyboardEvent => log.info(s"KeyboardEvent propagated to world/Not consumed by gui: $event")
          }
    }
  }

  private val mainMenu = new GameScreen[TestGame](this) {
    override def eventHandler: PartialFunction[GameEvent, Unit] = {
      case Render(time, _)    => guiSystem.draw(canvas, screenFitting = LinearShortestSide(reference = Vec2(640, 480)) * Constant(0.75f))
      case input: InputEvent  => input.filter(guiSystem)
    }
  }

  pda.push(mainMenu, ingame)

  def eventHandler: PartialFunction[GameEvent, Unit] = pda
}

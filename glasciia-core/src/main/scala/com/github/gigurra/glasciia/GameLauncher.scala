package com.github.gigurra.glasciia

import com.badlogic.gdx.{ApplicationListener, Gdx}
import com.github.gigurra.glasciia.GameEvent._
import com.github.gigurra.glasciia.Glasciia._
import com.github.gigurra.math.Vec2
import scala.language.implicitConversions

/**
  * Created by johan on 2016-10-30.
  * Helper class to not have to wait for an Init before creating OpenGL/GDX resources/objects
  */
class GameLauncher[R <: Resources, C <: Canvas](gameFactory: GameFactory[R, C]) extends ApplicationListener with Logging {

  private var stage: Game = _
  private var loadingScreen: Game = _
  private var resources: R = _
  private var canvas: Canvas = _
  private var firstFrame: Boolean = true

  override def create(): Unit = {
    log.info("Loading..")
    canvas = gameFactory.canvas()
    loadingScreen = gameFactory.loadingScreen(canvas)
    stage = loadingScreen
    resources = gameFactory.resources(canvas)
    Gdx.input.setListener(consumeEvent)
  }

  override def render(): Unit = {
    if (!firstFrame) {
      checkFinishedLoading()
    }
    canvas.setDrawTime()
    consumeEvent(Render(canvas.time, canvas))
    firstFrame = false
  }

  override def resize(width: Int, height: Int): Unit = consumeEvent(Resize(canvas.time, canvas, Vec2(width, height)))
  override def dispose(): Unit = consumeEvent(Exit(canvas.time, canvas))
  override def pause(): Unit = consumeEvent(Pause(canvas.time, canvas))
  override def resume(): Unit = consumeEvent(Resume(canvas.time, canvas))

  private def consumeEvent(event: GameEvent): Boolean = {
    stage.consume(event)
  }

  private def checkFinishedLoading(): Unit = {

    if (!resources.finished) {
      resources.load(gameFactory.loadingScreenFrameTime())
    }

    if (resources.finished && (stage eq loadingScreen)) {
      log.info("Loading done - launching game!")
      consumeEvent(Exit(canvas.time, canvas))
      stage = gameFactory.launch(resources, canvas)
    }
  }
}

object GameLauncher {
  def apply[R <: Resources, C <: Canvas](javaIfc: GameFactory[R, C]): GameLauncher[R, C] = new GameLauncher[R, C](javaIfc)
}

abstract class GameFactory[R <: Resources, C <: Canvas] {
  def canvas(): C
  def resources(canvas: Canvas): R
  def loadingScreen(canvas: Canvas): Game
  def loadingScreenFrameTime(): Long = 100
  def launch(resources: R, canvas: Canvas): Game
}

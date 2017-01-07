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
class GameLauncher[R <: Resources](gameFactory: GameFactory[R]) extends ApplicationListener with Logging {

  private var stage: Game = null.asInstanceOf[Game]
  private var loadingScreen: Game = null.asInstanceOf[Game]
  private var resources: R = null.asInstanceOf[R]
  private var firstFrame: Boolean = true

  override def create(): Unit = {
    log.info("Loading..")
    loadingScreen = gameFactory.loadingScreen()
    stage = loadingScreen
    resources = gameFactory.resources()
    Gdx.input.setListener(consumeEvent)
  }

  override def render(): Unit = {
    if (!firstFrame) {
      checkFinishedLoading()
    }
    stage.canvas.setDrawTime()
    consumeEvent(Render(stage.time, stage.canvas))
    firstFrame = false
  }

  override def resize(width: Int, height: Int): Unit = consumeEvent(Resize(stage.time, stage.canvas, Vec2(width, height)))
  override def dispose(): Unit = consumeEvent(Exit(stage.time, stage.canvas))
  override def pause(): Unit = consumeEvent(Pause(stage.time, stage.canvas))
  override def resume(): Unit = consumeEvent(Resume(stage.time, stage.canvas))

  private def consumeEvent(event: GameEvent): Boolean = {
    stage.consume(event)
  }

  private def checkFinishedLoading(): Unit = {

    if (!resources.finished) {
      resources.load(gameFactory.loadingScreenFrameTime())
    }

    if (resources.finished && (stage eq loadingScreen)) {
      log.info("Loading done - launching game!")
      consumeEvent(Exit(stage.time, stage.canvas))
      stage = gameFactory.launch(resources)
    }
  }
}

object GameLauncher {
  def apply[R <: Resources](javaIfc: GameFactory[R]): GameLauncher[R] = new GameLauncher[R](javaIfc)
}

abstract class GameFactory[R <: Resources] {
  def resources(): R
  def loadingScreen(): Game
  def loadingScreenFrameTime(): Long = 100
  def launch(resources: R): Game
}

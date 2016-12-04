package com.github.gigurra.glasciia

import com.github.gigurra.glasciia.GameEvent.InputEvent

/**
  * Created by johan on 2016-10-31.
  */
case class Act(scenes: Seq[Scene], var sceneIndex: Int = 0) extends InputEventHandler {
  require(sceneIndex >= 0, s"Cannot create scene with sceneIndex < 0")
  require(sceneIndex < scenes.length, s"Cannot create scene with sceneIndex >= scenes.length")

  def currentScene: Scene = scenes(sceneIndex)
  def finished: Boolean = _finished
  def size: Int = scenes.length
  def length: Int = size
  def last: Scene = scenes.last

  def update(time: Long): Unit = {
    if (!finished) {
      checkStartScene(time)
      updateScene(time)
      checkMoveToNextScene(time)
    }
  }

  def onEnd(): Unit = {}

  val inputHandler = new PartialFunction[InputEvent, Unit] {

    def actualHandler: PartialFunction[InputEvent, Unit] = currentScene.inputHandler

    override def isDefinedAt(event: InputEvent): Boolean = actualHandler.isDefinedAt(event)

    override def applyOrElse[A1 <: InputEvent, B1 >: Unit](event: A1, default: (A1) => B1): B1 = {
      if (!finished && isDefinedAt(event)) {
        apply(event)
      } else {
        default(event)
      }
    }

    override def apply(event: InputEvent): Unit = {
      actualHandler.apply(event)
    }
  }

  private def updateScene(time: Long): Unit = {
    currentScene.update(time - currentSceneStartedAt)
  }

  private def checkMoveToNextScene(time: Long): Unit = {
    if (currentScene.finished) {

      val prevSceneIndex = sceneIndex
      sceneIndex = math.min(length - 1, sceneIndex + 1)

      if (prevSceneIndex == length - 1) {
        _finished = true
        onEnd()
      }
    }
  }

  private def checkStartScene(time: Long): Unit = {
    if (!currentScene.begun) {
      currentScene.begin()
      currentSceneStartedAt = time
    }
  }

  private var _finished = scenes.isEmpty
  private var currentSceneStartedAt = 0L
}

object Act {
  def apply(scenes: Scene*): Act = new Act(scenes)
}

package com.github.gigurra.glasciia

/**
  * Created by johan on 2016-10-31.
  */
case class Act(t0: Long,
               scenes: Seq[Scene],
               var sceneIndex: Int) extends EventHandler {
  require(sceneIndex >= 0, s"Cannot create scene with sceneIndex < 0")
  require(sceneIndex < scenes.length, s"Cannot create scene with sceneIndex >= scenes.length")

  def currentScene: Scene = scenes(sceneIndex)
  def begun: Boolean = currentScene.begun
  def finished: Boolean = sceneIndex == length - 1 && last.finished
  def size: Int = scenes.length
  def length: Int = size
  def last: Scene = scenes.last

  val eventHandler = new PartialFunction[GameEvent, Unit] {

    def actualHandler: PartialFunction[GameEvent, Unit] = currentScene.eventHandler

    override def isDefinedAt(event: GameEvent): Boolean = actualHandler.isDefinedAt(event)

    override def applyOrElse[A1 <: GameEvent, B1 >: Unit](event: A1, default: (A1) => B1): B1 = {
      if (!finished && isDefinedAt(event)) {
        apply(event)
      } else {
        default(event)
      }
    }

    override def apply(event: GameEvent): Unit = {
      actualHandler.apply(event)
      if (currentScene.finished) {
        sceneIndex = math.min(length - 1, sceneIndex + 1)
      }
    }
  }
}

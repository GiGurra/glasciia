package com.github.gigurra.glasciia

import com.badlogic.gdx.Gdx

/**
  * Created by johan on 2016-09-19.
  * Must be used with a GameLauncher
  */
abstract class Game {

  def eventHandler: PartialFunction[GameEvent, Unit]
  def close(): Unit = Gdx.app.exit()

  private lazy val liftedEventHandler = eventHandler.lift

  private[glasciia] def consume(ev: GameEvent): Boolean = {
    liftedEventHandler.apply(ev) match {
      case Some(_) => true
      case None => false
    }
  }
}

/**
  * An empty game, for example for loading screens without an implementation
  */
class EmptyGame extends Game {
  override def eventHandler: PartialFunction[GameEvent, Unit] = {
    case _ =>
  }
}

class EmptyLoadingScreen extends Game {
  override def eventHandler: PartialFunction[GameEvent, Unit] = {
    case _ =>
  }
}

object Game {

  /**
    * Creates a new empty game, for example for loading screens without an implementation
    */
  def apply(): Game = {
    new EmptyGame
  }

  /**
    * Creates a new empty game, for example for loading screens without an implementation
    */
  def empty(): Game = {
    new EmptyGame
  }
}

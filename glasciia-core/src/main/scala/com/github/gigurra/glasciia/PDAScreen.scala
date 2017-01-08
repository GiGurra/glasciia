package com.github.gigurra.glasciia

import com.github.gigurra.glasciia.GameEvent.WindowEvent
import scala.language.implicitConversions

/**
  * Pushdown Automaton implementation
  */
class PDAScreen[State <: Screen](val pda: PDA[State]) extends Screen {
  def this() = this(PDA[State]())

  final val eventHandler: PartialFunction[GameEvent, Unit] = new PartialFunction[GameEvent, Unit] {
    override def applyOrElse[Event <: GameEvent, Result >: Unit](event: Event, preserve: (Event) => Result): Result = {
      event match {
        case windowEvent: WindowEvent   => handleWindowEvent(windowEvent); preserve(event)
        case _                          => handleInputEvent.applyOrElse(event, (_: Event) => preserve(event))
      }
    }
    override def isDefinedAt(x: GameEvent): Boolean = throw new RuntimeException("isDefinedAt Should not be called")
    override def apply(v1: GameEvent): Unit = throw new RuntimeException("apply Should not be called")
  }

  def handleWindowEvent(event: WindowEvent): Unit = {
    pda.reverse.foreach(_.eventHandler.applyOrElse(event, (_: WindowEvent) => ()))
  }

  protected def handleInputEvent: PartialFunction[GameEvent, Unit] = {
    pda.states.foldLeft(PartialFunction.empty[GameEvent, Unit])((acc, screen) => acc.orElse(screen.eventHandler) )
  }
}

object PDAScreen {

  def apply[State <: Screen](states: State*): PDAScreen[State] = {
    new PDAScreen[State](PDA[State](states : _*))
  }

  implicit def pdaScreen2Pda[State <: Screen](screen: PDAScreen[State]): PDA[State] = screen.pda
  implicit def pdaScreen2EventHandler[State <: Screen](screen: PDAScreen[State]): PartialFunction[GameEvent, Unit] = screen.eventHandler
}

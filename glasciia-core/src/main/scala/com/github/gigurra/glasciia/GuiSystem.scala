package com.github.gigurra.glasciia

import com.badlogic.gdx.Gdx
import com.github.gigurra.glasciia.GameEvent.InputEvent
import com.github.gigurra.glasciia.GuiSystem.Transition
import scala.language.implicitConversions

/**
  * Created by johan on 2017-01-01.
  */
case class GuiSystem(guis: Map[String, Gui], private var _active: String) {

  require(guis.contains(activeName), s"gui $activeName is not part of GuiSystem $this")

  private var transition: Option[Transition] = None
  private var transitionStartTime: Long = 0L
  private var transitionTotalTime: Long = 0L
  private var transitionFrom: Gui = null.asInstanceOf[Gui]
  private var transitionTo: Gui = null.asInstanceOf[Gui]

  def activeName: String = {
    _active
  }

  def activeGui: Gui = {
    guis(activeName)
  }

  def setActive(name: String): Unit = {
    require(guis.contains(name), s"gui $name is not part of GuiSystem $this")
    _active = name
  }

  def transition(to: String, transitionTime: Long, transition: Transition): Unit = {
    require(guis.contains(to), s"gui $to is not part of GuiSystem $this")
    this.transitionStartTime = timeMillis
    this.transitionTotalTime = transitionTime
    this.transition = Some(transition)
    this.transitionFrom = activeGui
    this.transitionTo = guis(to)
    setActive(to)
  }

  def draw(canvas: Canvas,
           dt: Float = Gdx.graphics.getDeltaTime,
           screenFitting: Scale = Scale.ONE,
           transform: Transform = Transform.IDENTITY): Unit = {

    transition match {
      case Some(t) =>
        t.draw(
          canvas = canvas,
          dt = dt,
          screenFitting = screenFitting,
          transform = transform,
          elapsed = transitionElapsed,
          transitionTime = transitionTotalTime,
          from = transitionFrom,
          to = transitionTo
        )

        if (transitionElapsed >= transitionTotalTime) {
          t.finish()
          transition = None
        }

      case None => canvas.drawGui(
        stage = activeGui,
        dt = dt,
        screenFitting = screenFitting,
        transform = transform
      )
    }
  }

  def inputHandler: PartialFunction[InputEvent, Unit] = {
    transition match {
      case Some(t) => t.inputHandler(transitionElapsed, transitionTotalTime, transitionFrom, transitionTo)
      case None => activeGui
    }
  }

  private def timeMillis: Long = {
    System.currentTimeMillis()
  }

  private def transitionElapsed: Long = {
    timeMillis - transitionStartTime
  }
}

object GuiSystem {

  def apply(guis: Map[String, Gui]): GuiSystem = {
    require(guis.nonEmpty, s"Must have at least one gui to create a ${classOf[GuiSystem].getSimpleName}")
    new GuiSystem(guis, guis.keys.head)
  }

  def apply(guis: (String, Gui)*): GuiSystem = {
    apply(guis.toMap)
  }

  implicit def system2inputHandler(system: GuiSystem): PartialFunction[InputEvent, Unit] = {
    system.inputHandler
  }

  trait Transition {

    def draw(canvas: Canvas,
             dt: Float,
             screenFitting: Scale,
             transform: Transform,
             elapsed: Long,
             transitionTime: Long,
             from: Gui,
             to: Gui): Unit

    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    // Optional overloads below

    def inputHandler(elapsed: Long, transitionTime: Long, from: Gui, to: Gui): PartialFunction[InputEvent, Unit] = {
      // from.act(dt) // By default, block actions/input into
      to
    }

    def finish(): Unit = {
      // By default do nothing on finish
    }
  }
}

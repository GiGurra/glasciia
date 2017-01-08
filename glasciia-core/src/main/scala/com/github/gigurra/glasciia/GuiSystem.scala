package com.github.gigurra.glasciia

import com.badlogic.gdx.Gdx
import com.github.gigurra.glasciia.GameEvent.InputEvent

import scala.collection.mutable
import scala.language.implicitConversions

/**
  * Created by johan on 2017-01-01.
  */
class GuiSystem(private val guis: mutable.Map[String, Gui],
                private var _activeName: Option[String]) extends Gui {

  require(activeName.forall(guis.contains), s"gui $activeName is not part of GuiSystem $this")

  private var transition: Option[GuiSystem.Transition] = None
  private var transitionStartTime: Long = 0L
  private var transitionTotalTime: Long = 0L
  private var transitionFrom: Option[Gui] = None
  private var transitionTo: Option[Gui] = None

  def add(name: String, gui: Gui): Option[Gui] = {
    guis.put(name, gui)
  }

  def remove(name: String): Option[Gui] = {
    require(!activeName.contains(name), s"Cannot remove the active gui: $name")
    guis.remove(name)
  }

  def activeName: Option[String] = {
    _activeName
  }

  def clear(): Unit = {
    guis.clear()
    _activeName = None
  }

  def size: Int = {
    guis.size
  }

  def isEmpty: Boolean = {
    guis.isEmpty
  }

  def nonEmpty: Boolean = {
    guis.nonEmpty
  }

  def guiNames: Vector[String] = {
    guis.keys.toVector
  }

  def activeGui: Option[Gui] = {
    activeName.flatMap(guis.get)
  }

  def setActive(name: String): Unit = {
    require(guis.contains(name), s"gui $name is not part of GuiSystem $this")
    _activeName = Some(name)
  }

  def setActive(name: Option[String]): Unit = {
    require(name.forall(guis.contains), s"gui $name is not part of GuiSystem $this")
    _activeName = name
  }

  def transition(to: String, transitionTime: Long, transition: GuiSystem.Transition): Unit = {
    require(guis.contains(to), s"gui $to is not part of GuiSystem $this")
    this.transitionStartTime = timeMillis
    this.transitionTotalTime = transitionTime
    this.transition = Some(transition)
    this.transitionFrom = activeGui
    this.transitionTo = guis.get(to)
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

      case None =>
        activeGui.foreach(_.draw(
          canvas = canvas,
          dt = dt,
          screenFitting = screenFitting,
          transform = transform
        ))
    }
  }

  override def inputHandler: PartialFunction[InputEvent, Unit] = {
    transition match {
      case Some(t) => t.inputHandler(transitionElapsed, transitionTotalTime, transitionFrom, transitionTo)
      case None => activeGui.map(_.inputHandler).getOrElse(PartialFunction.empty)
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
    new GuiSystem(new mutable.LinkedHashMap[String, Gui] ++= guis, guis.keys.headOption)
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
             from: Option[Gui],
             to: Option[Gui]): Unit

    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    // Optional overloads below

    def inputHandler(elapsed: Long, transitionTime: Long, from: Option[Gui], to: Option[Gui]): PartialFunction[InputEvent, Unit] = {
      // from.act(dt) // By default, block actions/input into
      to.map(_.inputHandler).getOrElse(PartialFunction.empty)
    }

    def finish(): Unit = {
      // By default do nothing on finish
    }
  }
}

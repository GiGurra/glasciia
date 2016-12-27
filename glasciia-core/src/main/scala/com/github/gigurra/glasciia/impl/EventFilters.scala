package com.github.gigurra.glasciia.impl

import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.gigurra.glasciia.GameEvent._
import com.github.gigurra.glasciia.GestureState
import com.github.gigurra.glasciia.Glasciia._

import scala.language.implicitConversions

/**
  * Created by johan on 2016-10-02.
  */
trait EventFilters {

  implicit def toFilterableEvent(event: InputEvent): EventFiltersImpl.FilterableInputEvent = {
    new EventFiltersImpl.FilterableInputEvent(event)
  }

  implicit def stage2Filter(stage: Stage): PartialFunction[InputEvent, Unit] = {
    if (stage.visible) {
      EventFiltersImpl.inputProcessor2Filter(stage)
    } else {
      PartialFunction.empty
    }
  }

}

object EventFilters extends EventFilters

object EventFiltersImpl {

  // Used for custom controllers and remapping of controls
  implicit class FilterableInputEvent(val event: InputEvent) extends AnyVal {

    private def returnEarlyIfConsumed(next: => InputEvent): InputEvent = {
      event match {
        case ConsumedEvent => ConsumedEvent
        case _ => next
      }
    }

    def fork(mappings: PartialFunction[InputEvent, Vector[InputEvent]]): Vector[InputEvent] = {
      mappings.applyOrElse(event, (_: Any) => Vector(event))
    }

    def foreach(filter: PartialFunction[InputEvent, Unit]): Unit = returnEarlyIfConsumed {
      filter.lift.apply(event)
      ConsumedEvent
    }

    def map(mappings: PartialFunction[InputEvent, InputEvent]): InputEvent = returnEarlyIfConsumed { mapIf(condition = true, mappings) }
    def mapIf(condition: Boolean, mappings: PartialFunction[InputEvent, InputEvent]): InputEvent = returnEarlyIfConsumed {
      if (condition) {
        mappings.applyOrElse(event, (_: Any) => event)
      } else {
        event
      }
    }

    def filter(mappings: PartialFunction[InputEvent, InputEvent], filter: PartialFunction[InputEvent, Unit]): InputEvent = filterIf(condition = true, mappings, filter)
    def filter(filter: PartialFunction[InputEvent, Unit]): InputEvent = filterIf(condition = true, mappings = PartialFunction.empty, filter)
    def filterIf(condition: Boolean, filter: PartialFunction[InputEvent, Unit]): InputEvent = filterIf(condition, mappings = PartialFunction.empty, filter)
    def filterIf(condition: Boolean, mappings: PartialFunction[InputEvent, InputEvent], filter: PartialFunction[InputEvent, Unit]): InputEvent = returnEarlyIfConsumed {
      if (condition) {
        val mappedEvent = mappings.lift.apply(event).getOrElse(event)
        filter.lift.apply(mappedEvent) match {
          case Some(_) => ConsumedEvent
          case None => event
        }
      } else {
        event
      }
    }

    def filterGestures(filter: PartialFunction[GestureEvent, Unit])(implicit state: GestureState): InputEvent = filterGestures(state, filter)
    def filterGestures(state: GestureState, filter: PartialFunction[GestureEvent, Unit]): InputEvent = filterGesturesIf(state, condition = true, filter)
    def filterGesturesIf(condition: Boolean, filter: PartialFunction[GestureEvent, Unit])(implicit state: GestureState): InputEvent = filterGesturesIf(state, condition, filter)
    def filterGesturesIf(state: GestureState, condition: Boolean, filter: PartialFunction[GestureEvent, Unit]): InputEvent = filterIf(condition, state.toInputProcessor(filter))
  }

  implicit def inputProcessor2Filter(inputProcessor: InputProcessor): PartialFunction[InputEvent, Unit] = new PartialFunction[InputEvent, Unit] {

    override def isDefinedAt(x: InputEvent): Boolean = throw new RuntimeException(s"Cannot make preemptive check if event will be consumed by $inputProcessor")

    override def applyOrElse[A1 <: InputEvent, B1 >: Unit](event: A1, default: (A1) => B1): B1 = {
      if (!tryApply(event)) {
        default(event)
      }
    }

    override def apply(event: InputEvent): Unit = {
      if (!tryApply(event)) {
        throw new IllegalArgumentException(s"$inputProcessor did not consume $event")
      }
    }

    private def tryApply(event: InputEvent): Boolean = event match {
      case kbEvent: KeyboardEvent => kbEvent match {
        case CharTyped(char)  => inputProcessor.keyTyped(char)
        case KeyDown(vKey)    => inputProcessor.keyDown(vKey)
        case KeyUp(vKey)      => inputProcessor.keyUp(vKey)
      }
      case msEvent: MouseEvent => msEvent match {
        case MouseMove(pos)           => inputProcessor.mouseMoved(pos.x.toInt, pos.y.toInt)
        case MouseScrolled(amount)    => inputProcessor.scrolled(amount)
        case TouchDown(pos, ptr, btn) => inputProcessor.touchDown(pos.x.toInt, pos.y.toInt, ptr, btn)
        case TouchUp(pos, ptr, btn)   => inputProcessor.touchUp(pos.x.toInt, pos.y.toInt, ptr, btn)
        case TouchDrag(pos, ptr)      => inputProcessor.touchDragged(pos.x.toInt, pos.y.toInt, ptr)
      }
      case _: ControllerEvent => false // TODO: Some kind of logging? Ignored: Needs to be mapped to something the gdx gui can understand..
      case ConsumedEvent => false
      case _ => false
    }
  }
}

package se.gigurra.glasciia.impl

import com.badlogic.gdx.InputProcessor
import se.gigurra.glasciia.AppEvent._
import se.gigurra.glasciia.{Gui, RootGui}

import scala.language.implicitConversions

/**
  * Created by johan on 2016-10-02.
  */
trait EventFilters {

  // Used for custom controllers and remapping of controls
  implicit class FilterableInputEvent(event: InputEvent) {

    private def returnEarlyIfConsumed(next: => InputEvent): InputEvent = {
      event match {
        case ConsumedEvent => ConsumedEvent
        case _ => next
      }
    }

    def map(mappings: PartialFunction[InputEvent, InputEvent]): InputEvent = returnEarlyIfConsumed {
      mappings.applyOrElse(event, (_: Any) => event)
    }

    def filter(mappings: PartialFunction[InputEvent, InputEvent], filter: PartialFunction[InputEvent, Unit]): InputEvent = event.map(mappings).filter(filter)
    def filter(filter: PartialFunction[InputEvent, Unit]): InputEvent = returnEarlyIfConsumed {
      filter.lift.apply(event).map(_ => ConsumedEvent).getOrElse(event)
    }

    def foreach(filter: PartialFunction[InputEvent, Unit]): Unit = returnEarlyIfConsumed {
      filter.lift.apply(event)
      ConsumedEvent
    }

    def filterIf(condition: => Boolean, mappings: PartialFunction[InputEvent, InputEvent], filter: PartialFunction[InputEvent, Unit]): InputEvent = event.map(mappings).filterIf(condition, filter)
    def filterIf(condition: => Boolean, filter: PartialFunction[InputEvent, Unit]): InputEvent = returnEarlyIfConsumed {
      if (condition) {
        this.filter(filter)
      } else {
        event
      }
    }
  }

  implicit def gui2Filter(gui: RootGui): PartialFunction[InputEvent, Unit] = {
    if (gui.hidden) {
      PartialFunction.empty
    } else {
      inputProcessor2Filter(gui.stage)
    }
  }

  implicit def inputProcessor2Filter(inputProcessor: InputProcessor): PartialFunction[InputEvent, Unit] = new PartialFunction[InputEvent, Unit] {

    override def isDefinedAt(x: InputEvent): Boolean = throw new RuntimeException(s"Cannot make preemptive check if event will be consumed by $inputProcessor")

    override def applyOrElse[A1 <: InputEvent, B1 >: Unit](event: A1, default: (A1) => B1): B1 = {
      tryApply(event) match {
        case true =>
        case false => default(event)
      }
    }

    override def apply(event: InputEvent): Unit = {
      tryApply(event) match {
        case true =>
        case false => throw new IllegalArgumentException(s"$inputProcessor did not consume $event")
      }
    }

    private def tryApply(event: InputEvent): Boolean = event match {
      case kbEvent: KeyboardEvent => kbEvent match {
        case CharTyped(char)  => inputProcessor.keyTyped(char)
        case KeyDown(vKey)    => inputProcessor.keyDown(vKey)
        case KeyUp(vKey)      => inputProcessor.keyUp(vKey)
      }
      case msEvent: MouseEvent => msEvent match {
        case MouseMove(pos)           => inputProcessor.mouseMoved(pos.x, pos.y)
        case MouseScrolled(amount)    => inputProcessor.scrolled(amount)
        case TouchDown(pos, ptr, btn) => inputProcessor.touchDown(pos.x, pos.y, ptr, btn)
        case TouchUp(pos, ptr, btn)   => inputProcessor.touchUp(pos.x, pos.y, ptr, btn)
        case TouchDrag(pos, ptr)      => inputProcessor.touchDragged(pos.x, pos.y, ptr)
      }
      case clEvent: ControllerEvent => false // TODO: Some kind of logging? Ignored: Needs to be mapped to something the gdx gui can understand..
      case ConsumedEvent => false
      case _ => false
    }
  }
}

object EventFilters extends EventFilters

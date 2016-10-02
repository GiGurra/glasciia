package se.gigurra.glasciia.impl

import com.badlogic.gdx.InputProcessor
import se.gigurra.glasciia.ApplicationEvent._
import scala.language.implicitConversions

/**
  * Created by johan on 2016-10-02.
  */
trait EventFilters {

  // Used for custom controllers and remapping of controls
  implicit class ConvertableInputEvent(event: InputEvent) {

    private def returnEarlyIfConsumed(next: => InputEvent): InputEvent = {
      event match {
        case ConsumedEvent => ConsumedEvent
        case _ => next
      }
    }

    def map(pf: PartialFunction[InputEvent, InputEvent]): InputEvent = returnEarlyIfConsumed {
      event match {
        case ConsumedEvent => ConsumedEvent
        case _ => pf.applyOrElse(event, (_: Any) => event)
      }
    }

    def filter(filter: PartialFunction[InputEvent, Boolean]): InputEvent = returnEarlyIfConsumed {
      filter.applyOrElse(event, (_: Any) => true) match {
        case true => event
        case false => ConsumedEvent
      }
    }

    def foreach(filter: PartialFunction[InputEvent, Unit]): Unit = returnEarlyIfConsumed {
      filter.lift.apply(event)
      ConsumedEvent
    }

    def filterIf(condition: => Boolean, filter: PartialFunction[InputEvent, Boolean]): InputEvent = returnEarlyIfConsumed {
      if (condition) {
        filter.applyOrElse(event, (_: Any) => true) match {
          case true => event
          case false => ConsumedEvent
        }
      } else {
        event
      }
    }
  }

  implicit def inputProcessor2Filter(inputProcessor: InputProcessor): PartialFunction[InputEvent, Boolean] = {
    case kbEvent: KeyboardEvent => kbEvent match {
      case CharTyped(char)  => !inputProcessor.keyTyped(char)
      case KeyDown(vKey)    => !inputProcessor.keyDown(vKey)
      case KeyUp(vKey)      => !inputProcessor.keyUp(vKey)
    }
    case msEvent: MouseEvent => msEvent match {
      case MouseMove(pos)           => !inputProcessor.mouseMoved(pos.x, pos.y)
      case MouseScrolled(amount)    => !inputProcessor.scrolled(amount)
      case TouchDown(pos, ptr, btn) => !inputProcessor.touchDown(pos.x, pos.y, ptr, btn)
      case TouchUp(pos, ptr, btn)   => !inputProcessor.touchUp(pos.x, pos.y, ptr, btn)
      case TouchDrag(pos, ptr)      => !inputProcessor.touchDragged(pos.x, pos.y, ptr)
    }
    case clEvent: ControllerEvent => true // TODO: Some kind of logging? Ignored: Needs to be mapped to something the gdx gui can understand..
    case ConsumedEvent => true
  }
}

object EventFilters extends EventFilters

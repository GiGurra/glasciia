package se.gigurra.glasciia

import com.badlogic.gdx.scenes.scene2d.ui.Table
import se.gigurra.glasciia.impl.GuiImplicits
import se.gigurra.math.Vec2

import scala.language.implicitConversions

/**
  * Created by johan on 2016-10-08.
  */
case class Gui[T_Table <: Table](table: T_Table,
                                 fillParent: Boolean = false,
                                 debug: Boolean = false) {

  table.setFillParent(fillParent)
  table.setDebug(debug)
}

object Gui extends GuiImplicits {
  implicit def gui2table[T <: Table](gui: Gui[T]): Table = gui.table.asInstanceOf[Table]
  implicit def gui2RichTable[T <: Table](gui: Gui[T]): TableOpsImplicits[T] = new TableOpsImplicits(gui.table)

  trait Scaling {
    def apply(drawBounds: Vec2[Int]): Float
    def *(otherScaling: Scaling): Scaling = {
      val self = this
      new Scaling {
        override def apply(drawBounds: Vec2[Int]): Float = {
          self.apply(drawBounds) * otherScaling.apply(drawBounds)
        }
      }
    }
  }

  object Scaling {
    case class LinearShortestSide(reference: Vec2[Int]) extends Scaling {
      override def apply(size: Vec2[Int]): Float = {
        math.min(size.x.toFloat / reference.x.toFloat, size.y.toFloat / reference.y.toFloat)
      }
    }
    case class Constant(constant: Float) extends Scaling {
      override def apply(size: Vec2[Int]): Float = {
        constant
      }
    }
    val ONE = Constant(1.0f)
  }
}

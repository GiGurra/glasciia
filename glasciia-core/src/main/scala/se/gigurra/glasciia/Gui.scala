package se.gigurra.glasciia

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import se.gigurra.glasciia.impl.GuiImplicits
import se.gigurra.math.Vec2

import scala.language.implicitConversions

/**
  * Created by johan on 2016-10-08.
  */
case class Gui(table: Table, fillParent: Boolean = false) {

  table.setFillParent(fillParent)

  def hidden: Boolean = !table.isVisible
  def stage: Stage = table.getStage

  def hide(): Unit = table.setVisible(false)
  def show(): Unit = table.setVisible(true)
}

object Gui extends GuiImplicits {
  implicit def gui2table[T <: Table](gui: Gui): Table = gui.table
  implicit def gui2RichTable[T <: Table](gui: Gui): TableOpsImplicits = new TableOpsImplicits(gui.table)

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

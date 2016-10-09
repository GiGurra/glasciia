package se.gigurra.glasciia

import com.badlogic.gdx.scenes.scene2d.ui.{Skin, Table}
import se.gigurra.glasciia.impl.GuiImplicits
import se.gigurra.math.Vec2

import scala.language.implicitConversions

/**
  * Created by johan on 2016-10-08.
  */

object Gui extends GuiImplicits {

  def apply(skin: Skin, debug: Boolean = false): Table = {
    val table = new Table(skin)
    table.setDebug(debug)
    table
  }

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

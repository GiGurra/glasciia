package com.github.gigurra.glasciia

import com.github.gigurra.math.Vec2

import scala.language.implicitConversions

/**
  * Created by johan on 2016-10-08.
  */
trait Scale {
  def apply(drawBounds: Vec2): Float
  def *(otherScaling: Scale): Scale = {
    val self = this
    new Scale {
      override def apply(drawBounds: Vec2): Float = {
        self.apply(drawBounds) * otherScaling.apply(drawBounds)
      }
    }
  }
}

object Scale {
  case class LinearShortestSide(reference: Vec2) extends Scale {
    override def apply(size: Vec2): Float = {
      math.min(size.x.toFloat / reference.x.toFloat, size.y.toFloat / reference.y.toFloat)
    }
  }
  case class Constant(constant: Float) extends Scale {
    override def apply(size: Vec2): Float = {
      constant
    }
  }
  val ONE = Constant(1.0f)
}

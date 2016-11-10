package com.github.gigurra.glasciia

/**
  * Created by johan on 2016-11-10.
  */
sealed trait Orientation
object Orientation {
  case object Landscape extends Orientation
  case object Portrait extends Orientation
}

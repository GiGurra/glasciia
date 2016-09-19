package se.gigurra.glasciia

import Specializable.Primitives

/**
  * Created by johan on 2016-09-19.
  */
case class Vec2[@specialized(Primitives) T : Numeric ](x: T, y: T) {

}

package se.gigurra.glasciia

import com.badlogic.gdx.Input.Keys
import se.gigurra.math.Vec2

/**
  * Created by johan on 2016-10-03.
  */
case class Pov4W(isKeyDownFunction: Int => Boolean,
                 left: Int = Keys.LEFT,
                 right: Int = Keys.RIGHT,
                 up: Int = Keys.UP,
                 down: Int = Keys.DOWN) {

  private def value(dir: Int): Int = if (isKeyDownFunction(dir)) 1 else 0

  def dir: Vec2[Int] = Vec2(
    x = value(right) - value(left),
    y = value(up) - value(down)
  )
}


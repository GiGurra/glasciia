package se.gigurra.glasciia.impl

import com.badlogic.gdx.Gdx
import se.gigurra.math.Vec2

/**
  * Created by johan on 2016-10-03.
  */
trait InputShortcuts {
  def mousePos: Vec2[Int] = Vec2(Gdx.input.getX, Gdx.input.getY)
}

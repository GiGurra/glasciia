package se.gigurra.glasciia.impl

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.{Cursor, Pixmap}
import se.gigurra.math.Vec2
import se.gigurra.glasciia.Glasciia._

/**
  * Created by johan on 2016-10-03.
  */
trait MouseFunctions {
  def mousePos: Vec2[Int] = Vec2(Gdx.input.getX, Gdx.input.getY)
  def setCursor(cursor: Cursor): Unit = Gdx.graphics.setCursor(cursor)
}

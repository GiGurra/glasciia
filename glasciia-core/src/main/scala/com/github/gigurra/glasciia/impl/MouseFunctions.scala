package com.github.gigurra.glasciia.impl

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Cursor
import com.github.gigurra.math.Vec2

/**
  * Created by johan on 2016-10-03.
  */
trait MouseFunctions {
  def mousePos: Vec2[Int] = Vec2(Gdx.input.getX, Gdx.input.getY)
  def mousePos(pointer: Int): Vec2[Int] = Vec2(Gdx.input.getX(pointer), Gdx.input.getY(pointer))
  def pinchPos: Vec2[Int] = (mousePos(0) + mousePos(1)) / 2
  def setCursor(cursor: Cursor): Unit = Gdx.graphics.setCursor(cursor)
}

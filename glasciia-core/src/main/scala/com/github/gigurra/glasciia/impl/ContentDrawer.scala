package com.github.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.github.gigurra.glasciia.Transform
import Transform._

/**
  * Created by johan on 2016-10-01.
  */
trait ContentDrawer {

  def batch: PolygonSpriteBatch

  def draw(transform: Transform)(content: => Unit): Unit = {
    batch.setTransformMatrix(transform)
    content
  }

  def draw(content: => Unit): Unit = {
    draw(Transform.IDENTITY)(content)
  }
}

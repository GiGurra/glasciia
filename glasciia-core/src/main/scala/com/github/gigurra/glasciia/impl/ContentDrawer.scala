package com.github.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.github.gigurra.glasciia.Transform
import com.badlogic.gdx.math.Matrix4

/**
  * Created by johan on 2016-10-01.
  */
trait ContentDrawer {

  private val tmp = new Matrix4

  def batch: PolygonSpriteBatch

  def draw(transform: Transform)(content: => Unit): Unit = {
    batch.setTransformMatrix(tmp.set(transform.data))
    content
  }

  def draw(content: => Unit): Unit = {
    draw(Transform.IDENTITY)(content)
  }
}

package com.github.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.g2d.SpriteBatcher
import com.github.gigurra.glasciia.Transform
import com.badlogic.gdx.math.Matrix4

/**
  * Created by johan on 2016-10-01.
  */
trait ContentDrawer {

  private val newMatrix = new Matrix4

  def batch: SpriteBatcher

  final def draw(transform: Transform)(content: => Unit): Unit = {

    val transformMatrixChanged = !fastEq(transform.data, batch.getTransformMatrix.`val`)

    if (transformMatrixChanged) {
      // TODO: Log bad API usage
      batch.setTransformMatrix(newMatrix.set(transform.data))
    }

    content
  }

  private def fastEq(ar1: Array[Float], ar2: Array[Float]): Boolean = {
    var i = 0
    while (i < 16) {
      if (ar1(i) != ar2(i)) {
        return false
      }
      i += 1
    }
    true
  }
}

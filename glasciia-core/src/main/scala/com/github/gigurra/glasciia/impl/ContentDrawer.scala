package com.github.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.github.gigurra.math.{One, Vec2, Zero}

/**
  * Created by johan on 2016-10-01.
  */
trait ContentDrawer {

  def batch: PolygonSpriteBatch

  val transform = Matrix4Stack(depth = 32)

  def draw(at: Vec2[Float] = Zero.vec2f,
           scale: Vec2[Float] = One.vec2f,
           rotate: Float = 0.0f,
           rotatePoint: Vec2[Float] = Zero.vec2f)(content: => Unit): Unit = {

    val needAt = notZero(at)
    val needScale = notOne(scale)
    val needRotate = rotate != 0.0f
    val needRotatePoint = notZero(rotatePoint)
    val needTransform = needAt || needScale || needRotate

    if (needTransform) {
      transform.pushPop(
        content = {
          if (needAt) transform.current.translate(at.x, at.y, 0.0f)
          if (needRotate) transform.current.rotate(0.0f, 0.0f, 1.0f, rotate)
          if (needRotatePoint) transform.current.translate(rotatePoint.x, rotatePoint.y, 0.0f)
          if (needScale) transform.current.scale(scale.x, scale.y, 1.0f)
          batch.setTransformMatrix(transform.current)
          content
        },
        after = {
          batch.setTransformMatrix(transform.current)
        }
      )
    } else {
      content
    }

  }

  private def notZero(v: Vec2[Float]): Boolean = {
    v.x != 0.0f || v.y != 0.0f
  }

  private def notOne(v: Vec2[Float]): Boolean = {
    v.x != 1.0f || v.y != 1.0f
  }

}

package se.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.g2d.Batch
import se.gigurra.math.{One, Vec2, Zero}

/**
  * Created by johan on 2016-10-01.
  */
trait ContentDrawer {

  def batch: Batch

  val transform = Matrix4Stack(depth = 32)

  def draw(at: Vec2[Float] = Vec2[Float](0.0f, 0.0f),
           scale: Vec2[Float] = Vec2[Float](1.0f, 1.0f),
           rotate: Float = 0.0f)(content: => Unit): Unit = {

    if (!batch.isDrawing)
      batch.begin()

    val needAt = notZero(at)
    val needScale = notOne(scale)
    val needRotate = rotate != 0.0f
    val needTransform = needAt || needScale || needRotate

    if (needTransform) {
      transform.pushPop(
        content = {
          if (needAt) transform.current.translate(at.x, at.y, 0.0f)
          if (needRotate) transform.current.rotate(0.0f, 0.0f, 1.0f, rotate)
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

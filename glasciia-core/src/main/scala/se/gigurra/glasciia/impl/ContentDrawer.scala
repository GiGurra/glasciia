package se.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.g2d.Batch
import se.gigurra.glasciia.math.Matrix4Stack
import se.gigurra.math.{One, Vec2, Zero}

/**
  * Created by johan on 2016-10-01.
  */
trait ContentDrawer {

  def batch: Batch

  val transform = Matrix4Stack(depth = 32)

  def draw(at: Vec2[Float] = Zero[Vec2[Float]],
           scale: Vec2[Float] = One[Vec2[Float]],
           rotate: Float = 0.0f)(content: => Unit): Unit = {

    if (!batch.isDrawing)
      batch.begin()

    val needAt = at != Zero[Vec2[Float]]
    val needScale = scale != One[Vec2[Float]]
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
}

package se.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.Batch
import se.gigurra.glasciia.math.Matrix4Stack

import scala.language.implicitConversions

/**
  * Created by johan on 2016-09-28.
  */
case class Transform[+T_Camera <: Camera](camera: T_Camera, depth: Int = 32) {
  private var dirty = true
  private val matrixStack = Matrix4Stack(depth, uploader = { m =>
    camera.view.set(m)
    dirty = true
  })

  def clean(): Unit ={
    if (dirty) {
      camera.update()
      dirty = false
    }
  }

  def load(batch: Batch): Unit = {
    clean()
    batch.setProjectionMatrix(camera.projection)
    batch.setTransformMatrix(camera.view)
  }
}

object Transform {
  implicit def proj2matstack[T_Camera <: Camera](p: Transform[T_Camera]): Matrix4Stack = p.matrixStack
}

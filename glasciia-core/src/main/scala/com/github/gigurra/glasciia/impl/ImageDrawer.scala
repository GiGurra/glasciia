package com.github.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.{Affine2, Vector2}
import com.github.gigurra.glasciia.Transform
import com.github.gigurra.math.Vec2

/**
  * Created by johan on 2016-10-01.
  */
trait ImageDrawer { self: ContentDrawer =>

  private val affine = new Affine2

  final def drawImage(image: TextureRegion, transform: Transform): Unit = {
    updateTransform(transform)
    draw(batch.draw(image, 1.0f, 1.0f, affine))
  }

  final def drawImageRepeated(image: TextureRegion, transform: Transform, count: Int, delta: Vec2): Unit = {
    updateTransform(transform)
    draw(batch.drawRepeat(image, 1.0f, 1.0f, affine, count, new Vector2(delta.x, delta.y)))
  }

  private final def updateTransform(transform: Transform): Unit = {
    val transformMatrix = transform.data
    affine.m00 = transformMatrix(0)
    affine.m01 = transformMatrix(4)
    affine.m02 = transformMatrix(12)
    affine.m10 = transformMatrix(1)
    affine.m11 = transformMatrix(5)
    affine.m12 = transformMatrix(13)
  }
}

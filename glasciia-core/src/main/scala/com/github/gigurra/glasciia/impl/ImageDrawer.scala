package com.github.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Affine2
import com.github.gigurra.glasciia.Transform

/**
  * Created by johan on 2016-10-01.
  */
trait ImageDrawer { self: ContentDrawer =>

  private val affine = new Affine2

  final def drawImage(image: TextureRegion, transform: Transform): Unit = {

    val other = transform.data

    affine.m00 = other(0)
    affine.m01 = other(4)
    affine.m02 = other(12)
    affine.m10 = other(1)
    affine.m11 = other(5)
    affine.m12 = other(13)

    draw(batch.draw(image, 1.0f, 1.0f, affine))
  }
}

package com.github.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.g2d.PolygonRegion
import com.badlogic.gdx.math.Affine2
import com.github.gigurra.glasciia.Transform

/**
  * Created by johan on 2016-10-01.
  */
trait PolygonDrawer extends ZOffset { self: ContentDrawer =>

  private val affine = new Affine2

  final def drawPolygon(polygon: PolygonRegion,
                        transform: Transform): Unit = {

    val other = transform.data

    affine.m00 = other(0)
    affine.m01 = other(4)
    affine.m02 = other(12)
    affine.m10 = other(1)
    affine.m11 = other(5)
    affine.m12 = other(13)

    draw(zTransform(transform))(batch.draw(polygon, affine))
  }
}

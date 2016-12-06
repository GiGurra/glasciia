package com.github.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.g2d.PolygonRegion
import com.github.gigurra.glasciia.Glasciia._
import com.github.gigurra.math.{One, Vec2, Zero}

/**
  * Created by johan on 2016-10-01.
  */
trait PolygonDrawer { self: ContentDrawer =>

  def drawPolygon(polygon: PolygonRegion,
                  at: Vec2[Float] = Zero.vec2f,
                  scale: Vec2[Float] = One.vec2f,
                  rotate: Float = 0.0f,
                  rotatePoint: Vec2[Float] = Zero.vec2f,
                  normalizeScale: Boolean = false): Unit = {

    val frameSize = polygon.regionSize
    val normalizedScale =
      if (normalizeScale)
        Vec2(scale.x / frameSize.x, scale.y / frameSize.y)
      else
        scale

    draw(at, normalizedScale, rotate, rotatePoint) {
      batch.draw(polygon, 0.0f, 0.0f)
    }
  }
}

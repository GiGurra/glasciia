package com.github.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.g2d.PolygonRegion
import com.github.gigurra.glasciia.Glasciia._
import com.github.gigurra.glasciia.Transform
import com.github.gigurra.math.Vec2

/**
  * Created by johan on 2016-10-01.
  */
trait PolygonDrawer { self: ContentDrawer =>

  def drawPolygon(polygon: PolygonRegion,
                  transform: Transform,
                  rotatePoint: Vec2 = Vec2.zero,
                  normalizeScale: Boolean = false): Unit = {

    val frameSize = polygon.regionSize
    val normalizedTransform: Transform =
      if (normalizeScale)
        transform.scale(1.0f / frameSize.x, 1.0f / frameSize.y)
      else
        transform

    draw(normalizedTransform) {
      batch.draw(polygon, 0.0f, 0.0f)
    }
  }
}

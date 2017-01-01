package com.github.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.g2d.PolygonRegion
import com.badlogic.gdx.math.Affine2
import com.github.gigurra.glasciia.Transform
import com.github.gigurra.glasciia.Glasciia._

/**
  * Created by johan on 2016-10-01.
  */
trait PolygonDrawer extends ZTranslationExtraction { self: ContentDrawer =>

  private val affine = new Affine2

  final def drawPolygon(polygon: PolygonRegion, transform: Transform): Unit = {
    affine.set(transform)
    draw(extractZTranslation(transform))(batch.draw(polygon, affine))
  }
}

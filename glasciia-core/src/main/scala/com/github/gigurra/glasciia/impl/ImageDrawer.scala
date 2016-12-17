package com.github.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.github.gigurra.math.Vec2
import com.github.gigurra.glasciia.Glasciia._
import com.github.gigurra.glasciia.Transform

/**
  * Created by johan on 2016-10-01.
  */
trait ImageDrawer { self: ContentDrawer =>

  def drawImage(image: TextureRegion,
                transform: Transform,
                rotatePoint: Vec2 = Vec2.zero,
                normalizeScale: Boolean = true): Unit = {

    val frameSize = image.size
    val normalizedTransform: Transform =
      if (normalizeScale)
        transform.scale(1.0f / frameSize.x, 1.0f / frameSize.y)
      else
        transform

    draw(normalizedTransform) {
      batch.draw(image, 0.0f, 0.0f)
    }
  }
}

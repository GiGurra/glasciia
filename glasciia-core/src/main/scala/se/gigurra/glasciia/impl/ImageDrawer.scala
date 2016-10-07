package se.gigurra.glasciia.impl

import se.gigurra.glasciia.Image
import se.gigurra.math.{Vec2, Zero}

/**
  * Created by johan on 2016-10-01.
  */
trait ImageDrawer { self: ContentDrawer =>

  def drawImage(image: Image,
                at: Vec2[Float] = Zero.vec2f,
                scale: Vec2[Float] = Vec2[Float](1.0f, 1.0f),
                rotate: Float = 0.0f,
                normalizeScale: Boolean = true): Unit = {

    val frameSize = image.size
    val normalizedScale =
      if (normalizeScale)
        Vec2(scale.x / frameSize.x, scale.y / frameSize.y)
      else
        scale

    draw(at, normalizedScale, rotate) {
      batch.draw(image.region, 0.0f, 0.0f)
    }
  }
}

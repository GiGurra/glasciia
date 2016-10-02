package se.gigurra.glasciia.impl

import java.time.Instant

import se.gigurra.glasciia.Animation
import se.gigurra.math.{One, Vec2, Zero}

/**
  * Created by johan on 2016-10-01.
  */
trait AnimationDrawer { self: ContentDrawer =>

  def drawAnimation(animation: Animation.Instance,
                    now: Instant = Instant.now,
                    at: Vec2[Float] = Zero[Vec2[Float]],
                    scale: Vec2[Float] = One[Vec2[Float]],
                    rotate: Float = 0.0f,
                    normalizeScale: Boolean = true): Unit = {

    val frameSize = animation.frameSize
    val normalizedScale =
      if (normalizeScale)
        Vec2(scale.x / frameSize.x.toFloat, scale.y / frameSize.y.toFloat)
      else
        scale

    draw(at, normalizedScale, rotate) {
      batch.draw(animation.currentFrame(now), 0.0f, 0.0f)
    }
  }
}
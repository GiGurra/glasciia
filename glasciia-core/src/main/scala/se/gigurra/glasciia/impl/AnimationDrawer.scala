package se.gigurra.glasciia.impl

import java.time.Instant

import se.gigurra.glasciia.Animation
import se.gigurra.math.{Vec2, Zero}

/**
  * Created by johan on 2016-10-01.
  */
trait AnimationDrawer { self: ContentDrawer =>

  def drawAnimation(animation: Animation.Instance,
                    now: Instant = Instant.now,
                    at: Vec2[Float] = Zero[Vec2[Float]],
                    scale: Float = 1.0f,
                    rotate: Float = 0.0f,
                    normalizeScale: Boolean = true): Unit = {

    val frameSize = animation.frameSize

    draw(at, if (normalizeScale) scale / frameSize.x.toFloat else scale, rotate) {
      batch.draw(animation.currentFrame(now), 0.0f, 0.0f)
    }
  }
}

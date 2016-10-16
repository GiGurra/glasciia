package se.gigurra.glasciia.impl

import se.gigurra.glasciia.Animation
import se.gigurra.math.{One, Vec2, Zero}

/**
  * Created by johan on 2016-10-01.
  */
trait AnimationDrawer { self: ContentDrawer =>

  def drawTime: Double

  def drawAnimation(animation: Animation.Instance,
                    at: Vec2[Float] = Zero.vec2f,
                    scale: Vec2[Float] = Zero.vec2f,
                    rotate: Float = 0.0f,
                    normalizeScale: Boolean = true,
                    active: Boolean = true,
                    time: Double = drawTime): Unit = {

    val frameSize = animation.frameSize
    val normalizedScale =
      if (normalizeScale)
        Vec2(scale.x / frameSize.x, scale.y / frameSize.y)
      else
        scale

    draw(at, normalizedScale, rotate) {
      batch.draw(animation.currentFrame(time, active), 0.0f, 0.0f)
    }
  }
}

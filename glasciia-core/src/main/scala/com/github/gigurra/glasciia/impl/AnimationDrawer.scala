package com.github.gigurra.glasciia.impl

import com.github.gigurra.glasciia.{Animation, Transform}

/**
  * Created by johan on 2016-10-01.
  */
trait AnimationDrawer { self: ContentDrawer =>

  def drawTime: Long

  def drawAnimation(animation: Animation.Instance,
                    transform: Transform,
                    normalizeScale: Boolean = true,
                    active: Boolean = true,
                    time: Long = drawTime): Unit = {

    val frameSize = animation.frameSize
    val normalizedTransform =
      if (normalizeScale)
        transform.scale(1.0f / frameSize.x, 1.0f / frameSize.y)
      else
        transform

    draw(normalizedTransform) {
      batch.draw(animation.currentFrame(time, active), 0.0f, 0.0f)
    }
  }
}

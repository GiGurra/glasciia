package com.github.gigurra.glasciia.impl

import com.github.gigurra.glasciia.{Animation, Transform}

/**
  * Created by johan on 2016-10-01.
  */
trait AnimationDrawer { self: ImageDrawer =>

  def time: Long

  def drawAnimation(animation: Animation.Instance,
                    transform: Transform,
                    active: Boolean = true,
                    time: Long = time): Unit = {

    drawImage(animation.currentFrame(time, active), transform)
  }
}

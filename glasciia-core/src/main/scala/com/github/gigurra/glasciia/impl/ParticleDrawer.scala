package com.github.gigurra.glasciia.impl

import com.badlogic.gdx.Gdx
import com.github.gigurra.glasciia.ParticleSource
import com.github.gigurra.math.Vec2

/**
  * Created by johan on 2016-10-02.
  */
trait ParticleDrawer { self: ContentDrawer =>

  def drawParticles(effect: ParticleSource, at: Vec2, angle: Float): Unit = {
    effect.setAngle(angle)
    drawParticles(effect, at)
  }

  def drawParticles(effect: ParticleSource, at: Vec2): Unit = {
    effect.setPosition(at.x, at.y)
    draw {
      effect.draw(batch, Gdx.graphics.getDeltaTime)
    }
  }
}

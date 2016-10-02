package se.gigurra.glasciia.impl

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import se.gigurra.math.Vec2

/**
  * Created by johan on 2016-10-02.
  */
trait ParticleEffectDrawer { self: ContentDrawer =>

  def drawEffect(effect: ParticleEffect, at: Vec2[Float]): Unit = {

    effect.setPosition(at.x, at.y)
    // TODO: Implement angling - Prob just by changing the angle of all emitters in the effect

    draw() {
      effect.draw(batch, Gdx.graphics.getDeltaTime)
    }
  }

  def drawEffect(effect: CollidingParticleEffect, at: Vec2[Float]): Unit = {

    effect.setPosition(at.x, at.y)
    // TODO: Implement angling - Prob just by changing the angle of all emitters in the effect

    draw() {
      effect.draw(batch, Gdx.graphics.getDeltaTime)
    }
  }
}

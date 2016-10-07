package se.gigurra.glasciia.impl

import com.badlogic.gdx.Gdx
import se.gigurra.glasciia.ParticleSource
import se.gigurra.math.Vec2

/**
  * Created by johan on 2016-10-02.
  */
trait ParticleDrawer { self: ContentDrawer =>

  def drawParticles(effect: ParticleSource, at: Vec2[Float], angle: Float): Unit = {
    effect.setAngle(angle)
    drawParticles(effect, at)
  }

  def drawParticles(effect: ParticleSource, at: Vec2[Float]): Unit = {
    effect.setPosition(at.x, at.y)
    draw() {
      effect.draw(batch, Gdx.graphics.getDeltaTime)
    }
  }
}

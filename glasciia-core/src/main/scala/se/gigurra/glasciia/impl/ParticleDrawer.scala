package se.gigurra.glasciia.impl

import com.badlogic.gdx.Gdx
import se.gigurra.glasciia.ParticleSource
import se.gigurra.math.Vec2

/**
  * Created by johan on 2016-10-02.
  */
trait ParticleDrawer { self: ContentDrawer =>

  def drawParticles(effect: ParticleSource,
                    at: Vec2[Float],
                    angle: Option[Float] = None): Unit = {
    effect.setPosition(at.x, at.y)
    angle.foreach(effect.setAngle)
    draw() {
      effect.draw(batch, Gdx.graphics.getDeltaTime)
    }
  }
}

package se.gigurra.glasciia.impl

import com.badlogic.gdx.Gdx
import se.gigurra.math.Vec2

/**
  * Created by johan on 2016-10-02.
  */
trait ParticleSourceDrawer { self: ContentDrawer =>

  def drawParticles(effect: ParticleSource,
                    at: Vec2[Float],
                    rotate: Option[Float] = None): Unit = {
    effect.setPosition(at.x, at.y)
    rotate.foreach(effect.setAngle)
    draw() {
      effect.draw(batch, Gdx.graphics.getDeltaTime)
    }
  }
}

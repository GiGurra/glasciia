package se.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.g2d.{ParticleEffect => GdxParticleEffect}
import se.gigurra.glasciia.{ParticleCollider, ParticleSource}

/**
  * Created by johan on 2016-10-02.
  */
trait ParticleSourceImplicits {

  implicit class RichCollidingEffect(effect: ParticleSource) {
    def copy: ParticleSource = new ParticleSource(effect)
    def copy(collider: ParticleCollider): ParticleSource = new ParticleSource(collider, effect)
  }

  implicit class RichGdxParticleEffectt(effect: GdxParticleEffect) {
    def copy: GdxParticleEffect = new GdxParticleEffect(effect)
  }
}

object ParticleSourceImplicits extends ParticleSourceImplicits

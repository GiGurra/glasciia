package se.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.g2d.{ParticleEffect => GdxParticleEffect}

/**
  * Created by johan on 2016-10-02.
  */
trait ParticleEffectImplicits {

  implicit class RichCollidingEffect(effect: CollidingParticleEffect) {
    def copy: CollidingParticleEffect = new CollidingParticleEffect(effect)
    def copy(collider: ParticleCollider): CollidingParticleEffect = new CollidingParticleEffect(collider, effect)
  }

  implicit class RichGdxParticleEffectt(effect: GdxParticleEffect) {
    def copy: GdxParticleEffect = new GdxParticleEffect(effect)
  }
}

object ParticleEffectImplicits extends ParticleEffectImplicits

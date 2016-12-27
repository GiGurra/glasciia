package com.github.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.g2d.{ParticleEffect => GdxParticleEffect}
import com.github.gigurra.glasciia.{ParticleCollider, ParticleSource}
import scala.language.implicitConversions

/**
  * Created by johan on 2016-10-02.
  */
trait ParticleSourceImplicits {
  import ParticleSourceImplicitsImpl._

  implicit def toRichCollidingEffect(effect: ParticleSource): RichCollidingEffect = {
    new RichCollidingEffect(effect)
  }

  implicit def toRichGdxParticleEffect(effect: GdxParticleEffect): RichGdxParticleEffect = {
    new RichGdxParticleEffect(effect)
  }
}

object ParticleSourceImplicits extends ParticleSourceImplicits

object ParticleSourceImplicitsImpl {

  implicit class RichCollidingEffect(val effect: ParticleSource) extends AnyVal {
    def copy: ParticleSource = new ParticleSource(effect)
    def copy(collider: ParticleCollider): ParticleSource = new ParticleSource(collider, effect)
  }

  implicit class RichGdxParticleEffect(val effect: GdxParticleEffect) extends AnyVal {
    def copy: GdxParticleEffect = new GdxParticleEffect(effect)
  }
}

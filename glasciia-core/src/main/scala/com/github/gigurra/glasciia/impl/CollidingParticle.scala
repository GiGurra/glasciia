package com.github.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.g2d.ParticleEmitter.Particle
import com.badlogic.gdx.graphics.g2d.Sprite
import com.github.gigurra.glasciia.ParticleCollider

/**
  * Created by johan on 2016-10-02.
  */
class CollidingParticle(sprite: Sprite, collider: ParticleCollider) extends Particle(sprite) {
  override def translate(xAmount: Float, yAmount: Float): Unit = {
    collider.collide(x = getX, y = getY, dx = xAmount, dy = yAmount) match {
      case Some(collision) => super.translate(collision.x, collision.y)
      case None => super.translate(xAmount, yAmount)
    }
  }
}

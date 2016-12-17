package com.github.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.g2d.ParticleEmitter.Particle
import com.badlogic.gdx.graphics.g2d.Sprite
import com.github.gigurra.glasciia.ParticleCollider
import com.github.gigurra.math.Vec2

import scala.language.postfixOps

/**
  * Created by johan on 2016-10-02.
  */
class CollidingParticle(sprite: Sprite, collider: ParticleCollider, deltaSecondsHolder: FloatHolder, minimumVelocity: Float) extends Particle(sprite) {
  override def translate(xAmount: Float, yAmount: Float): Unit = {
    val C = 0.0001
    val deltaTime: Float = deltaSecondsHolder.getValue
    val distance: Vec2[Float] = Vec2[Float](xAmount, yAmount)
println(velocity)
    val newDistance = if (deltaTime > 0) {
      val velocity = distance / deltaTime
      val dragAcceleration = math.pow(velocity.length, 2) * C
      this.velocity = velocity.norm.toFloat - (dragAcceleration * deltaTime).toFloat
    }


    collider.collide(x = getX, y = getY, dx = xAmount, dy = yAmount) match {
      case Some(collision) => super.translate(collision.x, collision.y)
      case None => super.translate(xAmount, yAmount)
    }
  }
}

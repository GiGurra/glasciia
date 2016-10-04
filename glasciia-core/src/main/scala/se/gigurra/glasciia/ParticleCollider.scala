package se.gigurra.glasciia

import se.gigurra.math.Vec2

trait ParticleCollider {
  def collide(x: Float, y: Float, dx: Float, dy: Float): Option[Vec2[Float]]
}

object ParticleCollider {
  val NO_COLLISIONS = new ParticleCollider {
    override def collide(x: Float, y: Float, dx: Float, dy: Float): Option[Vec2[Float]] = None
  }
}

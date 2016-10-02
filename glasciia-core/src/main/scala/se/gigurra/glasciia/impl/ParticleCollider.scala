package se.gigurra.glasciia.impl

import se.gigurra.math.Vec2

trait ParticleCollider {
  def collide(x: Float, y: Float, dx: Float, dy: Float): Option[Vec2[Float]]
}

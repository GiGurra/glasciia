package se.gigurra.glasciia.impl

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g2d.{ParticleEffect, TextureAtlas}

/**
  * Created by johan on 2016-10-02.
  */
object Particles {

  private implicit class Mutated[T](val t: T) extends AnyVal {
    def mutate(f: T => Unit): T = {
      f(t)
      t
    }
  }

  def collidingEffect(collider: ParticleCollider): CollidingParticleEffect = new CollidingParticleEffect(collider)
  def collidingEffect(collider: ParticleCollider, effectFile: FileHandle, imagesDir: FileHandle): CollidingParticleEffect = collidingEffect(collider).mutate(_.load(effectFile, imagesDir))
  def collidingEffect(collider: ParticleCollider, effectFile: FileHandle, atlas: TextureAtlas): CollidingParticleEffect = collidingEffect(collider).mutate(_.load(effectFile, atlas))
  def collidingEffect(collider: ParticleCollider, effectFile: FileHandle, atlas: TextureAtlas, atlasPrefix: String): CollidingParticleEffect = collidingEffect(collider).mutate(_.load(effectFile, atlas, atlasPrefix))

  def standardEffect(): ParticleEffect = new ParticleEffect()
  def standardEffect(effectFile: FileHandle, imagesDir: FileHandle): ParticleEffect = standardEffect().mutate(_.load(effectFile, imagesDir))
  def standardEffect(effectFile: FileHandle, atlas: TextureAtlas): ParticleEffect = standardEffect().mutate(_.load(effectFile, atlas))
  def standardEffect(effectFile: FileHandle, atlas: TextureAtlas, atlasPrefix: String): ParticleEffect = standardEffect().mutate(_.load(effectFile, atlas, atlasPrefix))
}

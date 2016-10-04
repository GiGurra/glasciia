package se.gigurra.glasciia

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g2d.TextureAtlas

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

  def collidingSource(collider: ParticleCollider): ParticleSource = new ParticleSource(collider)
  def collidingSource(collider: ParticleCollider, effectFile: FileHandle, imagesDir: FileHandle): ParticleSource = collidingSource(collider).mutate(_.load(effectFile, imagesDir))
  def collidingSource(collider: ParticleCollider, effectFile: FileHandle, atlas: TextureAtlas): ParticleSource = collidingSource(collider).mutate(_.load(effectFile, atlas))
  def collidingSource(collider: ParticleCollider, effectFile: FileHandle, atlas: TextureAtlas, atlasPrefix: String): ParticleSource = collidingSource(collider).mutate(_.load(effectFile, atlas, atlasPrefix))

  def standardSource(): ParticleSource = collidingSource(ParticleCollider.NO_COLLISIONS)
  def standardSource(effectFile: FileHandle, imagesDir: FileHandle): ParticleSource = standardSource().mutate(_.load(effectFile, imagesDir))
  def standardSource(effectFile: FileHandle, atlas: TextureAtlas): ParticleSource = standardSource().mutate(_.load(effectFile, atlas))
  def standardSource(effectFile: FileHandle, atlas: TextureAtlas, atlasPrefix: String): ParticleSource = standardSource().mutate(_.load(effectFile, atlas, atlasPrefix))
}

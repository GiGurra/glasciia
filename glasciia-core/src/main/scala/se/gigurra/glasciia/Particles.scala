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

  def colliding(collider: ParticleCollider): ParticleSource = new ParticleSource(collider)
  def colliding(collider: ParticleCollider, effectFile: FileHandle, imagesDir: FileHandle): ParticleSource = colliding(collider).mutate(_.load(effectFile, imagesDir))
  def colliding(collider: ParticleCollider, effectFile: FileHandle, atlas: TextureAtlas): ParticleSource = colliding(collider).mutate(_.load(effectFile, atlas))
  def colliding(collider: ParticleCollider, effectFile: FileHandle, atlas: TextureAtlas, atlasPrefix: String): ParticleSource = colliding(collider).mutate(_.load(effectFile, atlas, atlasPrefix))

  def standard(): ParticleSource = colliding(ParticleCollider.NO_COLLISIONS)
  def standard(effectFile: FileHandle, imagesDir: FileHandle): ParticleSource = standard().mutate(_.load(effectFile, imagesDir))
  def standard(effectFile: FileHandle, atlas: TextureAtlas): ParticleSource = standard().mutate(_.load(effectFile, atlas))
  def standard(effectFile: FileHandle, atlas: TextureAtlas, atlasPrefix: String): ParticleSource = standard().mutate(_.load(effectFile, atlas, atlasPrefix))
}

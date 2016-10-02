package se.gigurra.glasciia

import java.io.FileNotFoundException
import java.time.{Duration, Instant}

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.badlogic.gdx.graphics.g2d.{TextureRegion, Animation => GdxAnimation}
import com.badlogic.gdx.utils.{Array => GdxArray}
import se.gigurra.glasciia.util.LoadFile

/**
  * Created by johan on 2016-10-02.
  */
case class Animation(animation: GdxAnimation, dt: Duration) {

  val dtSeconds: Float = dt.toMillis.toFloat / 1000.0f

  def newInstance(t0: Instant = Instant.now): Animation.Instance = {
    new Animation.Instance(this, t0)
  }
}

object Animation {
  import scala.language.implicitConversions
  implicit def animToGdxAnim(animation: Animation): GdxAnimation = animation.animation

  def apply(source: String, nx: Int, ny: Int, dt: Duration): Animation = apply(source, nx, ny, dt, PlayMode.NORMAL)
  def apply(source: String, nx: Int, ny: Int, dt: Duration, mode: PlayMode): Animation = {
    apply(LoadFile(source).getOrElse(throw new FileNotFoundException(s"Could not find file '$source'")), nx = nx, ny = ny, dt = dt, mode = mode)
  }

  def apply(source: FileHandle, nx: Int, ny: Int, dt: Duration): Animation = apply(source, nx, ny, dt, PlayMode.NORMAL)
  def apply(source: FileHandle, nx: Int, ny: Int, dt: Duration, mode: PlayMode ): Animation = {
    val texture = new Texture(source)
    val tmp = TextureRegion.split(texture, texture.getWidth/nx, texture.getHeight/ny);              // #10
    val frames = new GdxArray[TextureRegion](nx * ny)
    for (iy <- 0 until ny) {
      for (jx <- 0 until nx) {
        frames.add(tmp(iy)(jx))
      }
    }
    new Animation(new GdxAnimation(dt.toMillis.toFloat / 1000.0f, frames, mode), dt)
  }

  case class Instance(animation: Animation, t0: Instant = Instant.now)  {
  }

  object Instance {
    import scala.language.implicitConversions
    implicit def instance2anim(instance: Instance): Animation = instance.animation
    implicit def instance2GdxAnim(instance: Instance): GdxAnimation = instance.animation.animation
  }
}

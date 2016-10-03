package se.gigurra.glasciia

import java.io.FileNotFoundException
import java.time.{Duration, Instant}

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.badlogic.gdx.graphics.g2d.{TextureRegion, Animation => GdxAnimation}
import com.badlogic.gdx.utils.{Array => GdxArray}
import se.gigurra.glasciia.util.LoadFile
import se.gigurra.math.Vec2

/**
  * Created by johan on 2016-10-02.
  */
case class Animation(animation: GdxAnimation,
                     dt: Duration,
                     frameSize: Vec2[Int]) {

  val dtSeconds: Float = dt.toMillis.toFloat / 1000.0f

  def newInstance(t0: Instant = Instant.now): Animation.Instance = {
    new Animation.Instance(this, t0)
  }

  def size: Vec2[Int] = frameSize
  def width: Int = size.x
  def height: Int = size.y
}

object Animation {
  import scala.language.implicitConversions
  implicit def animToGdxAnim(animation: Animation): GdxAnimation = animation.animation

  def apply(source: FileHandle,
            nx: Int,
            ny: Int,
            dt: Duration,
            mode: PlayMode = PlayMode.NORMAL,
            useMipMaps: Boolean = true,
            minFilter: Texture.TextureFilter = Texture.TextureFilter.MipMapLinearLinear,
            magFilter: Texture.TextureFilter = Texture.TextureFilter.Linear): Animation = {
    require(nx >= 0, s"Animation.apply: nx must be at least 1")
    require(nx >= 0, s"Animation.apply: ny must be at least 1")
    val texture = new Texture(source, useMipMaps)
    texture.setFilter(minFilter, magFilter)
    val tmp = TextureRegion.split(texture, texture.getWidth/nx, texture.getHeight/ny);              // #10
    val frames = new GdxArray[TextureRegion](nx * ny)
    for (iy <- 0 until ny) {
      for (jx <- 0 until nx) {
        frames.add(tmp(iy)(jx))
      }
    }
    val sz = Vec2(frames.get(0).getRegionWidth, frames.get(0).getRegionHeight)
    new Animation(new GdxAnimation(dt.toMillis.toFloat / 1000.0f, frames, mode), dt, sz)
  }

  case class Instance(animation: Animation, t0: Instant = Instant.now)  {
    def currentFrame(now: Instant = Instant.now()): TextureRegion = {
      val dt = Duration.between(t0, now).toMillis.toFloat / 1000.0f
      animation.getKeyFrame(dt)
    }
  }

  object Instance {
    import scala.language.implicitConversions
    implicit def instance2anim(instance: Instance): Animation = instance.animation
    implicit def instance2GdxAnim(instance: Instance): GdxAnimation = instance.animation.animation
  }
}

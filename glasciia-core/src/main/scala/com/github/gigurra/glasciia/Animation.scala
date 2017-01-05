package com.github.gigurra.glasciia

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.badlogic.gdx.graphics.g2d.{TextureRegion, Animation => GdxAnimation}
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.utils.{Array => GdxArray}
import com.github.gigurra.math.Vec2

/**
  * Created by johan on 2016-10-02.
  */
case class Animation(animation: GdxAnimation[TextureRegion],
                     frameSize: Vec2) {

  def dt: Long = (animation.getFrameDuration.toDouble * 1000.0).toLong
  def newInstance(t0: Long): Animation.Instance = new Animation.Instance(this, t0)
  def size: Vec2 = frameSize
  def width: Float = size.x
  def height: Float = size.y
}

object Animation {
  import scala.language.implicitConversions
  implicit def animToGdxAnim(animation: Animation): GdxAnimation[TextureRegion] = animation.animation

  def fromFile(source: FileHandle,
               nx: Int,
               ny: Int,
               dt: Long,
               mode: PlayMode = PlayMode.NORMAL,
               useMipMaps: Boolean = true,
               minFilter: Texture.TextureFilter = Texture.TextureFilter.MipMapLinearLinear,
               magFilter: Texture.TextureFilter = Texture.TextureFilter.Linear): Animation = {
    val texture = new Texture(source, useMipMaps)
    texture.setFilter(minFilter, magFilter)
    apply(
      region = new TextureRegion(texture),
      nx = nx,
      ny = ny,
      dt = dt,
      mode = mode
    )
  }

  def apply(region: TextureRegion,
            nx: Int,
            ny: Int,
            dt: Long,
            mode: PlayMode = PlayMode.NORMAL): Animation = {
    require(nx >= 0, s"Animation.apply: nx must be at least 1")
    require(nx >= 0, s"Animation.apply: ny must be at least 1")
    val frames = region.split(region.getRegionWidth / nx, region.getRegionHeight / ny).flatten
    apply(frames, dt, mode)
  }

  def apply(frames: Seq[TextureRegion],
            dt: Long): Animation = {
    apply(frames, dt, PlayMode.NORMAL)
  }

  def apply(frames: Seq[TextureRegion],
            dt: Long,
            mode: PlayMode): Animation = {
    require(frames.nonEmpty, "Cannot create animation without any frames")

    val gdxArray = new GdxArray[TextureRegion](frames.toArray)
    val sz = Vec2(frames.head.getRegionWidth, frames.head.getRegionHeight)
    new Animation(new GdxAnimation((dt.toDouble / 1000.0).toFloat, gdxArray, mode), sz)
  }

  case class Instance(animation: Animation, t0: Long) {
    private var lastFrameTime = t0
    private var tAcc = 0L
    private var active = true
    def currentFrame(now: Long): TextureRegion = {
      if (active)
        tAcc += math.max(0L, now - lastFrameTime)
      lastFrameTime = now
      animation.getKeyFrame((tAcc.toDouble / 1000.0).toFloat)
    }
    def currentFrame(now: Long, active: Boolean): TextureRegion = {
      this.active = active
      currentFrame(now)
    }
    def stop(): Unit = active = false
    def continue(): Unit = active = true
    def restart(): Unit = { tAcc = 0L; active = true }
    def asDrawable(timeFunc: => Long): Drawable = new ChangingRegionDrawable(currentFrame(timeFunc))
  }

  object Instance {
    import scala.language.implicitConversions
    implicit def instance2anim(instance: Instance): Animation = instance.animation
    implicit def instance2GdxAnim(instance: Instance): GdxAnimation[TextureRegion] = instance.animation.animation
  }
}

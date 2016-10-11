package se.gigurra.glasciia

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.badlogic.gdx.graphics.g2d.{TextureRegion, Animation => GdxAnimation}
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.utils.{Array => GdxArray}
import se.gigurra.math.Vec2

/**
  * Created by johan on 2016-10-02.
  */
case class Animation(animation: GdxAnimation,
                     dt: Double,
                     frameSize: Vec2[Int]) {

  def newInstance(t0: Double): Animation.Instance = {
    new Animation.Instance(this, t0)
  }

  def size: Vec2[Int] = frameSize
  def width: Int = size.x
  def height: Int = size.y
}

object Animation {
  import scala.language.implicitConversions
  implicit def animToGdxAnim(animation: Animation): GdxAnimation = animation.animation

  def fromFile(source: FileHandle,
               nx: Int,
               ny: Int,
               dt: Double,
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
            dt: Double,
            mode: PlayMode = PlayMode.NORMAL): Animation = {
    require(nx >= 0, s"Animation.apply: nx must be at least 1")
    require(nx >= 0, s"Animation.apply: ny must be at least 1")
    val tmp = region.split(region.getRegionWidth / nx, region.getRegionHeight / ny)
    val frames = new GdxArray[TextureRegion](nx * ny)
    for (iy <- 0 until ny) {
      for (jx <- 0 until nx) {
        frames.add(tmp(iy)(jx))
      }
    }
    val sz = Vec2(frames.get(0).getRegionWidth, frames.get(0).getRegionHeight)
    new Animation(new GdxAnimation(dt.toFloat, frames, mode), dt, sz)
  }

  case class Instance(animation: Animation, t0: Double) {
    private var lastFrameTime = t0
    private var tAcc = 0.0
    private var active = true
    def currentFrame(now: Double): TextureRegion = {
      if (active)
        tAcc += math.max(0.0f, now - lastFrameTime)
      lastFrameTime = now
      animation.getKeyFrame(tAcc.toFloat)
    }
    def currentFrame(now: Double, active: Boolean): TextureRegion = {
      this.active = active
      currentFrame(now)
    }
    def stop(): Unit = active = false
    def continue(): Unit = active = true
    def restart(): Unit = { tAcc = 0.0; active = true }
    def asDrawable(timeFunc: => Double): Drawable = new ChangingRegionDrawable(currentFrame(timeFunc))
  }

  object Instance {
    import scala.language.implicitConversions
    implicit def instance2anim(instance: Instance): Animation = instance.animation
    implicit def instance2GdxAnim(instance: Instance): GdxAnimation = instance.animation.animation
  }
}

package com.github.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.math.{Matrix4, Vector3}

/**
  * Created by johan on 2016-10-19.
  */
case class Matrix4Stack(depth: Int) {
  private val stack = (0 until depth).map(_ => new Matrix4).toArray
  private var i = 0

  def current = stack(i)

  def next = stack(i + 1)

  def push() = {
    require(i + 1 < depth, "Matrix4Stack push overflow!")
    next.set(current)
    i += 1
  }

  def pop() = {
    require(i > 0, "Matrix4Stack pop underflow!")
    i -= 1
  }

  def pushPop(content: => Unit, after: => Unit = ()): Unit = {
    push()
    try {
      content
    } finally {
      pop()
      after
    }
  }

  def apply[AnyReturn](ftr: Matrix4Stack => AnyReturn)(f: => Unit): Unit = {
    pushPop({ftr(this); f})
  }

  def unitSize(texture: GlyphLayout) = scale(
    1.0f / math.min(texture.width, texture.height),
    1.0f / math.min(texture.width, texture.height))

  def center(text: GlyphLayout) = translate(-text.width / 2.0f, text.height / 2.0f)
  def centerX(texture: Texture) = translate(-texture.getWidth.toFloat / 2.0f, 0.0f)
  def centerX(text: GlyphLayout) = translate(-text.width / 2.0f, 0.0f)
  def centerY(texture: Texture) = translate(0.0f, -texture.getHeight.toFloat / 2.0f)
  def centerY(text: GlyphLayout) = translate(0.0f, text.height / 2.0f)

  def loadIdentity(): Matrix4Stack = {
    current.idt()
    this
  }

  def transform(f: Matrix4 => Unit): this.type = { f(current); this }
  def translate(t: Vector3): this.type = transform(_.translate(t))
  def translate(x: Float = 0.0f, y: Float = 0.0f, z: Float = 0.0f): this.type = transform(_.translate(x, y, z))
  def scale(t: Vector3): this.type = transform(_.scale(t.x, t.y, t.z))
  def scale(x: Float = 1.0f, y: Float = 1.0f, z: Float = 1.0f): this.type = transform(_.scale(x, y, z))
  def scalexy(s: Float): this.type = scale(x = s, y = s)
  def scalexy(s: Double): this.type = scalexy(s.toFloat)
  def inverseScaleXY(): this.type = scale(1.0f / current.getScaleX, 1.0f / current.getScaleY)
  def overrideScaleXY(s: Float): this.type = scale(x = s / current.getScaleX, y = s / current.getScaleY)
  def rotate(angle: Float, x: Float = 0.0f, y: Float = 0.0f, z: Float = 0.0f): this.type = transform(_.rotate(angle, x, y, z))
  def rotate(angle: Float, axis: Vector3): this.type = transform(_.rotate(axis, angle))
  def rotate(angle: Float): this.type = transform(_.rotate(Vector3.Z, angle))
  def rotate(angle: Double): this.type = rotate(angle.toFloat)
}

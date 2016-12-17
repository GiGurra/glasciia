package com.github.gigurra.glasciia.impl

import java.util

import com.badlogic.gdx.math.{MathUtils, Matrix4}

/**
  * Created by johan on 2016-12-17.
  * Mixing the best of LWJGL (no statics) with the best of libgdx (fast jni transforms)
  */
case class Mat4Mutable(values: Array[Float]) {
  require(values.length == 16, "Must be of matrix size 4x4")

  final def setIdentity(): Mat4Mutable = {
    set(Mat4Mutable.identityValues)
    this
  }

  final def setZero(): Mat4Mutable = {
    set(Mat4Mutable.zeroValues)
    this
  }

  final def set(src: Mat4Mutable): Mat4Mutable = {
    set(src.values)
    this
  }

  final def set(newValues: Array[Float]): Mat4Mutable = {
    System.arraycopy(newValues, 0, this.values, 0, 16)
    this
  }
  
  final def add(other: Mat4Mutable): Mat4Mutable = {
    values(0) += other.values(0)
    values(1) += other.values(1)
    values(2) += other.values(2)
    values(3) += other.values(3)
    values(4) += other.values(4)
    values(5) += other.values(5)
    values(6) += other.values(6)
    values(7) += other.values(7)
    values(8) += other.values(8)
    values(9) += other.values(9)
    values(10) += other.values(10)
    values(11) += other.values(11)
    values(12) += other.values(12)
    values(13) += other.values(13)
    values(14) += other.values(14)
    values(15) += other.values(15)
    this
  }

  final def sub(other: Mat4Mutable): Mat4Mutable = {
    values(0) -= other.values(0)
    values(1) -= other.values(1)
    values(2) -= other.values(2)
    values(3) -= other.values(3)
    values(4) -= other.values(4)
    values(5) -= other.values(5)
    values(6) -= other.values(6)
    values(7) -= other.values(7)
    values(8) -= other.values(8)
    values(9) -= other.values(9)
    values(10) -= other.values(10)
    values(11) -= other.values(11)
    values(12) -= other.values(12)
    values(13) -= other.values(13)
    values(14) -= other.values(14)
    values(15) -= other.values(15)
    this
  }

  final def mul(other: Mat4Mutable): Mat4Mutable = {
    Matrix4.mul(values, other.values)
    this
  }

  final def scale(sx: Float, sy: Float, sz: Float): Mat4Mutable = {
    values(0) *= sx
    values(1) *= sx
    values(2) *= sx
    values(3) *= sx
    values(4) *= sy
    values(5) *= sy
    values(6) *= sy
    values(7) *= sy
    values(8) *= sz
    values(9) *= sz
    values(10) *= sz
    values(11) *= sz
    this
  }

  final def rotateDegrees(axisX: Float, axisY: Float, axisZ: Float, degrees: Float): Mat4Mutable = {
    rotateRads(axisX, axisY, axisZ, degrees.toRadians)
  }

  final def rotateRads(axisX: Float, axisY: Float, axisZ: Float, radians: Float): Mat4Mutable = {
    val c = Math.cos(radians).toFloat
    val s = Math.sin(radians).toFloat
    val oneMinusCos = 1.0f - c
    val xy = axisX * axisY
    val yz = axisY * axisZ
    val xz = axisX * axisZ
    val xs = axisX * s
    val ys = axisY * s
    val zs = axisZ * s

    val f00 = axisX * axisX * oneMinusCos + c
    val f01 = xy * oneMinusCos + zs
    val f02 = xz * oneMinusCos - ys
    val f10 = xy * oneMinusCos - zs
    val f11 = axisY * axisY * oneMinusCos + c
    val f12 = yz * oneMinusCos + xs
    val f20 = xz * oneMinusCos + ys
    val f21 = yz * oneMinusCos - xs
    val f22 = axisZ * axisZ * oneMinusCos + c

    val t00 = values(0) * f00 + values(4) * f01 + values(8) * f02
    val t01 = values(1) * f00 + values(5) * f01 + values(9) * f02
    val t02 = values(2) * f00 + values(6) * f01 + values(10) * f02
    val t03 = values(3) * f00 + values(7) * f01 + values(11) * f02
    val t10 = values(0) * f10 + values(4) * f11 + values(8) * f12
    val t11 = values(1) * f10 + values(5) * f11 + values(9) * f12
    val t12 = values(2) * f10 + values(6) * f11 + values(10) * f12
    val t13 = values(3) * f10 + values(7) * f11 + values(11) * f12
    values(8) = values(0) * f20 + values(4) * f21 + values(8) * f22
    values(9) = values(1) * f20 + values(5) * f21 + values(9) * f22
    values(10) = values(2) * f20 + values(6) * f21 + values(10) * f22
    values(11) = values(3) * f20 + values(7) * f21 + values(11) * f22
    values(0) = t00
    values(1) = t01
    values(2) = t02
    values(3) = t03
    values(4) = t10
    values(5) = t11
    values(6) = t12
    values(7) = t13
    this
  }

  final def translate(dx: Float, dy: Float, dz: Float) {
    values(12) += values(0) * dx + values(4) * dy + values(8) * dz
    values(13) += values(1) * dx + values(5) * dy + values(9) * dz
    values(14) += values(2) * dx + values(6) * dy + values(10) * dz
    values(15) += values(3) * dx + values(7) * dy + values(11) * dz
  }

  final def transpose(): Mat4Mutable = {
    val t00 = this.values(0)
    val t01 = this.values(4)
    val t02 = this.values(8)
    val t03 = this.values(12)
    val t10 = this.values(1)
    val t11 = this.values(5)
    val t12 = this.values(9)
    val t13 = this.values(13)
    val t20 = this.values(2)
    val t21 = this.values(6)
    val t22 = this.values(10)
    val t23 = this.values(14)
    val t30 = this.values(3)
    val t31 = this.values(7)
    val t32 = this.values(11)
    val t33 = this.values(15)

    this.values(0) = t00
    this.values(1) = t01
    this.values(2) = t02
    this.values(3) = t03
    this.values(4) = t10
    this.values(5) = t11
    this.values(6) = t12
    this.values(7) = t13
    this.values(8) = t20
    this.values(9) = t21
    this.values(10) = t22
    this.values(11) = t23
    this.values(12) = t30
    this.values(13) = t31
    this.values(14) = t32
    this.values(15) = t33

    this
  }

  final def invert(): Mat4Mutable = {
    if (Matrix4.inv(values)) {
      this
    } else {
      throw new RuntimeException(s"Cannot invert matrix with determinant 0: $this")
    }
  }

  final def negate(): Mat4Mutable = {
    values(0) = -values(0)
    values(1) = -values(1)
    values(2) = -values(2)
    values(3) = -values(3)
    values(4) = -values(4)
    values(5) = -values(5)
    values(6) = -values(6)
    values(7) = -values(7)
    values(8) = -values(8)
    values(9) = -values(9)
    values(10) = -values(10)
    values(11) = -values(11)
    values(12) = -values(12)
    values(13) = -values(13)
    values(14) = -values(14)
    values(15) = -values(15)
    this
  }

  final def getScaleX: Float = if (MathUtils.isZero(values(Matrix4.M01)) && MathUtils.isZero(values(Matrix4.M02))) Math.abs(values(Matrix4.M00)) else Math.sqrt(getScaleXSquared).toFloat
  final def getScaleY: Float = if (MathUtils.isZero(values(Matrix4.M10)) && MathUtils.isZero(values(Matrix4.M12))) Math.abs(values(Matrix4.M11)) else Math.sqrt(getScaleYSquared).toFloat
  final def getScaleZ: Float = if (MathUtils.isZero(values(Matrix4.M20)) && MathUtils.isZero(values(Matrix4.M21))) Math.abs(values(Matrix4.M22)) else Math.sqrt(getScaleZSquared).toFloat
  final def getScaleXSquared: Float = values(Matrix4.M00) * values(Matrix4.M00) + values(Matrix4.M01) * values(Matrix4.M01) + values(Matrix4.M02) * values(Matrix4.M02)
  final def getScaleYSquared: Float = values(Matrix4.M10) * values(Matrix4.M10) + values(Matrix4.M11) * values(Matrix4.M11) + values(Matrix4.M12) * values(Matrix4.M12)
  final def getScaleZSquared: Float = values(Matrix4.M20) * values(Matrix4.M20) + values(Matrix4.M21) * values(Matrix4.M21) + values(Matrix4.M22) * values(Matrix4.M22)

}

object Mat4Mutable {
  def copyFrom(source: Array[Float]): Mat4Mutable = Mat4Mutable(util.Arrays.copyOf(source, 16))
  def apply(): Mat4Mutable = Mat4Mutable(util.Arrays.copyOf(identityValues, 16))
  private val identityValues: Array[Float] = Array(1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f )
  private val zeroValues: Array[Float]     = Array(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f )
}

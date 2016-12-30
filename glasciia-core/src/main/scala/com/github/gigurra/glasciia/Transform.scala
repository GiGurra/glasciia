package com.github.gigurra.glasciia

import java.util

import com.badlogic.gdx.math.Matrix4
import com.github.gigurra.glasciia.impl.Mat4Mutable

import scala.language.implicitConversions
import com.github.gigurra.math.{Vec2, Vec3, Vec4}

/**
  * Immutable transform class
  */
case class Transform(private val impl: Mat4Mutable) {

  final def data: Array[Float] = impl.values
  final def scaleX: Float = impl.getScaleX
  final def scaleY: Float = impl.getScaleY
  final def scaleZ: Float = impl.getScaleZ
  final def zTranslation: Float = impl.values(14)

  final def *(v: Vec2): Vec2 = {
    Vec2(
      v.x * data(Matrix4.M00) + v.y * data(Matrix4.M01) + data(Matrix4.M03),
      v.x * data(Matrix4.M10) + v.y * data(Matrix4.M11) + data(Matrix4.M13))
  }

  final def *(v: Vec3): Vec3 = {
    Vec3(
      v.x * data(Matrix4.M00) + v.y * data(Matrix4.M01) + v.z * data(Matrix4.M02) + data(Matrix4.M03),
      v.x * data(Matrix4.M10) + v.y * data(Matrix4.M11) + v.z * data(Matrix4.M12) + data(Matrix4.M13),
      v.x * data(Matrix4.M20) + v.y * data(Matrix4.M21) + v.z * data(Matrix4.M22) + data(Matrix4.M23))
  }

  final def *(v: Vec4): Vec4 = {
    val vv: Vec4 = v.normalizeByW
    Vec4(
      vv.x * data(Matrix4.M00) + vv.y * data(Matrix4.M01) + vv.z * data(Matrix4.M02) + data(Matrix4.M03),
      vv.x * data(Matrix4.M10) + vv.y * data(Matrix4.M11) + vv.z * data(Matrix4.M12) + data(Matrix4.M13),
      vv.x * data(Matrix4.M20) + vv.y * data(Matrix4.M21) + vv.z * data(Matrix4.M22) + data(Matrix4.M23),
      1.0f)
  }

  override def hashCode(): Int = {
    util.Arrays.hashCode(data)
  }

  override def equals(obj: scala.Any): Boolean = {
    obj match {
      case otherTransform: Transform => util.Arrays.equals(otherTransform.data, data)
      case _ => false
    }
  }
}

object Transform {
  val IDENTITY: Transform = new Transform(Mat4Mutable())
  val Z_AXIS: Vec3 = new Vec3(0.0f, 0.0f, 1.0f)

  private val Vec2One = Vec2(1.0f, 1.0f)

  implicit def toTransformBuilder(t: Transform): TransformBuilder = new TransformBuilder(Mat4Mutable.copyFrom(t.data))
  implicit def toTransform(t: TransformBuilder): Transform = new Transform(t.m)

  def apply(): Transform = {
    IDENTITY
  }

  def apply(data: Array[Float]): Transform = {
    new Transform(Mat4Mutable(data))
  }

  def apply(at: Vec2 = Vec2.zero,
            angle: Float = 0.0f,
            scale: Vec2 = Vec2One): Transform = {
    Transform.translate(at).rotate(angle).scale(scale)
  }

  /**
    * Used to minimize allocation of new matrices
    * Note: You should NEVER allocate or hold a reference to a TransformBuilder
    */
  final class TransformBuilder(val m: Mat4Mutable) extends AnyVal {

    final def inverse: TransformBuilder = {
      m.invert()
      this
    }

    final def rotate(degrees: Float, axis: Vec3 = Z_AXIS): TransformBuilder = {
      m.rotateDegrees(axis.x, axis.y, axis.z, degrees)
      this
    }

    final def translate(x: Float = 0.0f, y: Float = 0.0f, z: Float = 0.0f): TransformBuilder = {
      m.translate(x, y, z)
      this
    }

    final def translate(v: Vec2): TransformBuilder = {
      translate(v.x, v.y)
    }

    final def translate(v: Vec3): TransformBuilder = {
      translate(v.x, v.y, v.z)
    }

    final def preTranslate(x: Float = 0.0f, y: Float = 0.0f, z: Float = 0.0f): TransformBuilder = {
      m.values(12) += x
      m.values(13) += x
      m.values(14) += x
      this
    }

    final def preTranslate(v: Vec2): TransformBuilder = {
      preTranslate(v.x, v.y)
    }

    final def preTranslate(v: Vec3): TransformBuilder = {
      preTranslate(v.x, v.y, v.z)
    }

    final def scale(x: Float = 1.0f, y: Float = 1.0f, z: Float = 1.0f): TransformBuilder = {
      m.scale(x, y, z)
      this
    }

    final def scale(v: Vec2): TransformBuilder = {
      scale(v.x, v.y)
    }

    final def scale(v: Vec3): TransformBuilder = {
      scale(v.x, v.y, v.z)
    }

    final def mul(m2: Array[Float]): TransformBuilder = {
      Matrix4.mul(m.values, m2)
      this
    }

    final def mul(m2: Mat4Mutable): TransformBuilder = {
      m.mul(m2)
      this
    }

    final def mul(m2: Transform): TransformBuilder = {
      mul(m2.data)
      this
    }

    final def setIdentity(): TransformBuilder = {
      m.setIdentity()
      this
    }

  }

  implicit def toBuilder(co: Transform.type): TransformBuilder = {
    new TransformBuilder(Mat4Mutable())
  }
}

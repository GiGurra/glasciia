package com.github.gigurra.glasciia

import java.util

import com.badlogic.gdx.math.{Matrix4, Quaternion, Vector3}
import com.github.gigurra.glasciia.impl.MatrixWithFunctions.MatrixWithFunctions

import scala.language.implicitConversions
import com.github.gigurra.math.{Vec2, Vec3, Zero}

/**
  * Immutable transform class
  */
case class Transform(private val m: Matrix4) { // Private because it is sucky libgdx mutable matrix

  override def hashCode(): Int = {
    util.Arrays.hashCode(m.`val`)
  }

  override def equals(obj: scala.Any): Boolean = {
    obj match {
      case otherTransform: Transform => util.Arrays.equals(otherTransform.m.`val`, m.`val`)
      case _ => false
    }
  }
}

object Transform {
  val IDENTITY: Transform = new Transform(new Matrix4())
  val Z_AXIS: Vec3[Float] = new Vec3[Float](0.0f, 0.0f, 1.0f)

  private val Vec2One = Vec2[Float](1.0f, 1.0f)
  private val gdxZAxis = new Vector3(0.0f, 0.0f, 1.0f)

  implicit def getFunctions(t: Transform): MatrixWithFunctions = new MatrixWithFunctions(t.m)

  implicit def toMatrix(t: Transform): Matrix4 = t.m
  implicit def toTransformBuilder(t: Transform): TransformBuilder = new TransformBuilder(new Matrix4(t.m))
  implicit def toTransform(t: TransformBuilder): Transform = new Transform(t.m)

  def apply(): Transform = {
    IDENTITY
  }

  def apply(at: Vec2[Float] = Zero.vec2f,
            angle: Float = 0.0f,
            scale: Vec2[Float] = Vec2One): Transform = {
    new Transform(new Matrix4(new Vector3(at.x, at.y, 0.0f), new Quaternion(gdxZAxis, angle), new Vector3(scale.x, scale.y, 1.0f)))
  }

  /**
    * Used to minimize allocation of new matrices
    * Note: You should NEVER allocate or hold a reference to a TransformBuilder
    */
  class TransformBuilder(val m: Matrix4) extends AnyVal {
    def inverse: TransformBuilder = mutate(_.inv())
    def rotate(degrees: Float, axis: Vec3[Float] = Z_AXIS): TransformBuilder = mutate(_.rotate(axis.x, axis.y, axis.z, degrees))
    def translate(x: Float = 0.0f, y: Float = 0.0f, z: Float = 0.0f): TransformBuilder = mutate(_.translate(x, y, z))
    def translate(v: Vec2[Float]): TransformBuilder = translate(v.x, v.y)
    def translate(v: Vec3[Float]): TransformBuilder = translate(v.x, v.y, v.z)
    def scale(x: Float = 1.0f, y: Float = 1.0f, z: Float = 1.0f): TransformBuilder = mutate(_.scale(x, y, z))
    def scale(v: Vec2[Float]): TransformBuilder = scale(v.x, v.y)
    def scale(v: Vec3[Float]): TransformBuilder = scale(v.x, v.y, v.z)
    def mul(m2: Matrix4): TransformBuilder = mutate(_.mul(m2))

    private def mutate(f: Matrix4 => Unit): TransformBuilder = {
      f(m)
      this
    }
  }

  implicit def toBuilder(co: Transform.type): TransformBuilder = {
    new TransformBuilder(new Matrix4())
  }
}

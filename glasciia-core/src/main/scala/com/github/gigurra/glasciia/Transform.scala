package com.github.gigurra.glasciia

import com.badlogic.gdx.math.{Matrix4, Quaternion, Vector3}
import com.github.gigurra.glasciia.impl.MatrixWithFunctions.MatrixWithFunctions

import scala.language.implicitConversions
import Transform.Z_AXIS
import com.github.gigurra.math.{Vec2, Vec3, Zero}

/**
  * Immutable transform class
  */
case class Transform(private val m: Matrix4) { // Private because it is sucky libgdx mutable matrix

  def inverse: Transform = makeNew(_.inv())
  def rotate(degrees: Float, axis: Vec3[Float] = Z_AXIS): Transform = makeNew(_.rotate(axis.x, axis.y, axis.z, degrees))
  def translate(x: Float = 0.0f, y: Float = 0.0f, z: Float = 0.0f): Transform = makeNew(_.translate(x, y, z))
  def translate(v: Vec2[Float]): Transform = translate(v.x, v.y)
  def translate(v: Vec3[Float]): Transform = translate(v.x, v.y, v.z)
  def scale(x: Float = 1.0f, y: Float = 1.0f, z: Float = 1.0f): Transform = makeNew(_.scale(x, y, z))
  def scale(v: Vec2[Float]): Transform = scale(v.x, v.y)
  def scale(v: Vec3[Float]): Transform = scale(v.x, v.y, v.z)
  def mul(m2: Matrix4): Transform = makeNew(_.mul(m2))

  private def makeNew(f: Matrix4 => Unit): Transform = {
    val out = new Matrix4(m)
    f(out)
    new Transform(out)
  }
}

object Transform {
  val IDENTITY: Transform = new Transform(new Matrix4())
  val Z_AXIS: Vec3[Float] = new Vec3[Float](0.0f, 0.0f, 1.0f)
  private val gdxZAxis = new Vector3(0.0f, 0.0f, 1.0f)

  implicit def getFunctions(t: Transform): MatrixWithFunctions = new MatrixWithFunctions(t.m)

  implicit def toMatrix(t: Transform): Matrix4 = t.m

  def apply(): Transform = {
    IDENTITY
  }

  def apply(at: Vec2[Float] = Zero.vec2f,
            angle: Float = 0.0f,
            scale: Vec2[Float] = Zero.vec2f): Transform = {
    new Transform(new Matrix4(new Vector3(at.x, at.y, 0.0f), new Quaternion(gdxZAxis, angle), new Vector3(scale.x, scale.y, 1.0f)))
  }
}

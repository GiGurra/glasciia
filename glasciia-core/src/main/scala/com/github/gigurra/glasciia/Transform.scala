package com.github.gigurra.glasciia

import com.badlogic.gdx.math.Matrix4
import com.github.gigurra.glasciia.impl.MatrixWithFunctions.MatrixWithFunctions

import scala.language.implicitConversions
import Transform.IDENTITY
import Transform.Z_AXIS
import com.github.gigurra.glasciia.impl.MatImplicits._
import com.github.gigurra.math.Vec3

/**
  * Immutable transform class
  */
case class Transform(private val m: Matrix4 = IDENTITY.m) { // Private because it is sucky libgdx mutable matrix

  def inverse: Transform = makeNew(_.inv())
  def rotate(degrees: Float, axis: Vec3[Float] = Z_AXIS): Transform = makeNew(_.rotate(axis.x, axis.y, axis.z, degrees))
  def translate(x: Float = 0.0f, y: Float = 0.0f, z: Float = 0.0f): Transform = makeNew(_.translate(x, y, z))
  def scale(x: Float = 1.0f, y: Float = 1.0f, z: Float = 1.0f): Transform = makeNew(_.scale(x, y, z))
  def mul(m2: Matrix4): Transform = makeNew(_.mul(m2))

  private def makeNew(f: Matrix4 => Unit, m0: Matrix4 = m): Transform = {
    val out = new Matrix4(m0)
    f(out)
    out
  }
}

object Transform {
  val IDENTITY: Transform = new Transform(new Matrix4())
  val Z_AXIS: Vec3[Float] = new Vec3[Float](0.0f, 0.0f, 1.0f)
  implicit def getFunctions(m: Transform): MatrixWithFunctions = new MatrixWithFunctions(m.m)
}
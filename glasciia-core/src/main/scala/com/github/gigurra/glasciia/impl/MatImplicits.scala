package com.github.gigurra.glasciia.impl

import com.badlogic.gdx.math.{Matrix4, Vector3}
import com.github.gigurra.math.{Vec2, Vec3, Vec4}
import MatrixWithFunctions.MatrixWithFunctions
import com.github.gigurra.glasciia.Transform

import scala.language.implicitConversions

/**
  * Created by johan on 2016-09-28.
  */
trait MatImplicits {
  implicit def getFunctions(m: Matrix4): MatrixWithFunctions = new MatrixWithFunctions(m)
  implicit def getFunctions(m: Matrix4Stack): MatrixWithFunctions = new MatrixWithFunctions(m.current)
  implicit def m2Transform(m: Matrix4): Transform = new Transform(m)
  implicit def s2Transform(s: Matrix4Stack): Transform = new Transform(s.current)
}

object MatImplicits extends MatImplicits

object MatrixWithFunctions {

  class MatrixWithFunctions(val m: Matrix4) extends AnyVal {

    def *(v: Vec2[Float]): Vec2[Float] = {
      val gdxVec = new Vector3(v.x, v.y, 0.0f)
      gdxVec.mul(m)
      Vec2(gdxVec.x, gdxVec.y)
    }

    def *(v: Vec3[Float]): Vec3[Float] = {
      val gdxVec = new Vector3(v.x, v.y, v.z)
      gdxVec.mul(m)
      Vec3(gdxVec.x, gdxVec.y, gdxVec.z)
    }

    def *(v: Vec4[Float]): Vec4[Float] = {
      val wNormalized = v.normalizeByW
      val gdxVec = new Vector3(wNormalized.x, wNormalized.y, wNormalized.z)
      gdxVec.mul(m)
      Vec4(gdxVec.x, gdxVec.y, gdxVec.z, 1.0f)
    }
  }
}

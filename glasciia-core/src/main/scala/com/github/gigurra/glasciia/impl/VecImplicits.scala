package com.github.gigurra.glasciia.impl

import com.badlogic.gdx.math.{Vector2, Vector3}
import com.github.gigurra.math.{Vec2, Vec3}

import scala.language.implicitConversions

/**
  * Created by johan on 2016-09-28.
  */
trait VecImplicits {
  implicit def vecToGdxVec(vec: Vec2): Vector2 = new Vector2(vec.x, vec.y)
  implicit def gdxVec2Vec(vec: Vector2): Vec2 = Vec2(vec.x, vec.y)

  implicit def vecToGdxVec3(vec: Vec3): Vector3 = new Vector3(vec.x, vec.y, vec.z)
  implicit def gdxVec2Vec3(vec: Vector3): Vec3 = Vec3(vec.x, vec.y, vec.z)

  implicit def vecToGdxVec23(vec: Vec2): Vector3 = new Vector3(vec.x, vec.y, 0.0f)
  implicit def gdxVec2Vec23(vec: Vector2): Vec3 = Vec3(vec.x, vec.y, 0.0f)

  implicit def vecToGdxVec32(vec: Vec3): Vector2 = new Vector2(vec.x, vec.y)
  implicit def gdxVec2Vec32(vec: Vector3): Vec2 = Vec2(vec.x, vec.y)
}

object VecImplicits extends VecImplicits

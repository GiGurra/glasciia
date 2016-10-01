package se.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.Matrix4
import se.gigurra.glasciia.math.Matrix4Stack

import scala.language.implicitConversions

/**
  * Created by johan on 2016-09-28.
  */
case class Transform(target: Matrix4, depth: Int = 32) {
  private val matrixStack = Matrix4Stack(depth, uploader = m => target.set(m) )
}

object Transform {
  implicit def trans2matstack(p: Transform): Matrix4Stack = p.matrixStack
}

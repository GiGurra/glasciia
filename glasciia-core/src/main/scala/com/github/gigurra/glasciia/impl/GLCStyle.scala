package com.github.gigurra.glasciia.impl

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20

import scala.language.implicitConversions

trait GLCStyle extends GlConstants {
  def gl: GL20 = Gdx.gl20
  implicit def gl2gl(self: GLCStyle): GL20 = gl
}
/**
  * Created by johan on 2016-09-27.
  */
object GLCStyle extends GLCStyle

package se.gigurra.glasciia

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import se.gigurra.glasciia.impl.GlConstants

import scala.language.implicitConversions

trait CStyleGL extends GlConstants {
  def gl: GL20 = Gdx.gl20
  implicit def gl2gl(self: CStyleGL): GL20 = gl
}
/**
  * Created by johan on 2016-09-27.
  */
object CStyleGL extends CStyleGL

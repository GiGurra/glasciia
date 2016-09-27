package se.gigurra.glasciia.impl

import com.badlogic.gdx.{Gdx, Graphics}
import se.gigurra.glasciia.{GLCStyle, Window}

/**
  * Created by johan on 2016-09-27.
  */
trait GlWindowFunctions extends GLCStyle {  _: Window =>

  def drawFrame(content: => Unit): Unit = {
    gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
    content
  }

}

object GlWindowFunctions {
  import scala.language.implicitConversions
  implicit def window2funcs(glwf: GlWindowFunctions): Graphics = Gdx.graphics
}

package se.gigurra.glasciia

import com.badlogic.gdx.graphics.{Camera, Color, OrthographicCamera, PerspectiveCamera}
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import se.gigurra.glasciia.impl.Transform
import se.gigurra.math.Vec2

/**
  * Created by johan on 2016-09-29.
  */
case class Canvas(defaultColor: Color = Color.GREEN,
                  defaultBackgroundColor: Color = Color.GRAY) extends Glasciia {

  val batch = new SpriteBatch
  val orthoTransform = Transform(new OrthographicCamera)
  val perspectiveTransform = Transform(new PerspectiveCamera)
  var transform: Transform[Camera] = orthoTransform

  def loadTransform(): Unit = {
    transform.load(batch)
  }

  def drawFrame(background: Color = defaultBackgroundColor)(content: => Unit): Unit = {
    gl.glClearColor(background.r, background.g, background.b, background.a)
    gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
    loadTransform()
    content
  }

  def draw(c: Char,
           pos: Vec2[Float],
           size: Float,
           bold: Boolean = false,
           italic: Boolean = false,
           foreground: Color = defaultColor,
           background: Color = defaultBackgroundColor,
           doLoadTransform: Boolean = true): Unit = {

    if (doLoadTransform) loadTransform()

    ???
  }
}

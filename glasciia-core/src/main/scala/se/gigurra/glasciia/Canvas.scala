package se.gigurra.glasciia

import com.badlogic.gdx.graphics.{Color, OrthographicCamera, PerspectiveCamera}
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import se.gigurra.glasciia.math.Matrix4Stack
import se.gigurra.math.{Vec2, Zero}

/**
  * Created by johan on 2016-09-29.
  */
case class Canvas(defaultColor: Color = Color.GREEN,
                  defaultBackgroundColor: Color = Color.GRAY) extends Glasciia {

  val batch = new SpriteBatch
  val orthographicCamera = new OrthographicCamera
  val perspectiveCamera = new PerspectiveCamera
  var camera = orthographicCamera
  val transform = Matrix4Stack(depth = 32, uploader = camera.view.set(_))

  def drawFrame(background: Color = defaultBackgroundColor)(content: => Unit): Unit = {
    gl.glClearColor(background.r, background.g, background.b, background.a)
    gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
    camera.update()
    batch.setProjectionMatrix(camera.projection)
    batch.setTransformMatrix(camera.view)
    content
  }

  def drawChar(c: Char,
               at: Vec2[Float] = Zero[Vec2[Float]],
               scale: Float = 1.0f,
               rotate: Float = 0.0f,
               bold: Boolean = false,
               italic: Boolean = false,
               foreground: Color = defaultColor,
               background: Color = defaultBackgroundColor): Unit = {

    draw(at, scale, rotate) {
      ???
    }
  }

  def draw(at: Vec2[Float] = Zero[Vec2[Float]],
           scale: Float = 1.0f,
           rotate: Float = 0.0f)(content: => Unit): Unit = {

    transform.pushPop {
      if (scale != 1.0f) transform.current.scale(scale, scale, 1.0f)
      if (rotate != 0.0f) transform.current.rotate(0.0f, 0.0f, 1.0f, rotate)
      if (at != Zero[Vec2[Float]]) transform.current.translate(-at.x, at.y, 0.0f)
      transform.upload()
      content
    }
  }
}

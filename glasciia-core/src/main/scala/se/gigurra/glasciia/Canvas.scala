package se.gigurra.glasciia

import com.badlogic.gdx.graphics.{Camera, Color, OrthographicCamera, PerspectiveCamera}
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import se.gigurra.glasciia.math.Matrix4Stack
import se.gigurra.math.{Vec2, Zero}

import scala.reflect.ClassTag

/**
  * Created by johan on 2016-09-29.
  */
case class Canvas(app: App) extends Glasciia {

  val batch = new SpriteBatch
  val orthographicCamera = new OrthographicCamera
  val perspectiveCamera = new PerspectiveCamera
  var camera: Camera = orthographicCamera
  private val transform = Matrix4Stack(depth = 32)

  def drawFrame(background: Color)(content: => Unit): Unit = {
    gl.glClearColor(background.r, background.g, background.b, background.a)
    gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
    drawSubFrame(content)
  }

  def drawSubFrame(content: => Unit): Unit = {
    camera.update()
    batch.setProjectionMatrix(camera.projection)
    batch.setTransformMatrix(camera.view)
    transform.pushPop {
      transform.current.mul(camera.view)
      content
    }
  }

  def drawChar(c: Char,
               font: Font,
               color: Color,
               at: Vec2[Float] = Zero[Vec2[Float]],
               scale: Float = 1.0f,
               rotate: Float = 0.0f): Unit = {
    draw(at, scale, rotate) {
      font.font.setColor(color)
      font.font.draw(batch, "c", 0.0f, 0.0f)
    }
  }

  def draw(at: Vec2[Float] = Zero[Vec2[Float]],
           scale: Float = 1.0f,
           rotate: Float = 0.0f)(content: => Unit): Unit = {

    val needAt = at != Zero[Vec2[Float]]
    val needScale = scale != 1.0f
    val needRotate = rotate != 0.0f
    val needTransform = needAt || needScale || needRotate

    if (needTransform) {
      transform.pushPop(
        content = {
          if (needAt) transform.current.translate(-at.x, -at.y, 0.0f)
          if (needRotate) transform.current.rotate(0.0f, 0.0f, 1.0f, rotate)
          if (needScale) transform.current.scale(scale, scale, 1.0f)
          batch.setTransformMatrix(transform.current)
          content
        },
        after = {
          batch.setTransformMatrix(transform.current)
        }
      )
    } else {
      content
    }

  }

  def setOrtho(yDown: Boolean, width: Float, height: Float): Unit = {
    orthographicCamera.setToOrtho(yDown, width, height)
    camera = orthographicCamera
  }

  def size: Vec2[Int] = app.size
  def width: Int = app.width
  def height: Int = app.height

}

package se.gigurra.glasciia

import ApplicationEvent._
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import se.gigurra.glasciia.conf.{GlConf, WindowConf}
import se.gigurra.glasciia.impl.{ApplicationEventListener, LwjglImplementation, ResourceManager}
import se.gigurra.glasciia.util.Shader
import se.gigurra.math.Vec2

import scala.util.Random

/**
  * Created by johan on 2016-09-26.
  */
object OpenWindowTest {

  def main(args: Array[String]): Unit = {

    val initialWindowConf = WindowConf(
      position = Vec2(100, 100),
      size = Vec2(640, 480),
      resizable = false,
      maximized = false,
      title = "Test Window"
    )

    val initialGlConf = GlConf(
      vsync = true,
      msaa = 4,
      foregroundFpsCap = None,
      backgroundFpsCap = Some(30)
    )

    val app = new App(initialWindowConf, initialGlConf)
      with ApplicationEventListener
      with ResourceManager
      with LwjglImplementation

    app.storeResource[Font]("font:monospace-default", Font.fromTtfFile("pt-mono/PTM55FT.ttf"), _.close())
    app.storeResource[ShaderProgram]("shader:default", Shader.fromLocation("shaders/default-shader.vert", "shaders/default-shader.frag"), _.dispose())

    app.handleEvents {

      case Init(canvas) =>

        val otherShader = app.resource[ShaderProgram]("shader:default")
        canvas.batch.setShader(otherShader)

        println()
        println("VERTEX SHADER SOURCE")
        println("--------------------")
        println(canvas.batch.getShader.getVertexShaderSource)
        println()
        println("FRAGMENT SHADER SOURCE")
        println("----------------------")
        println(canvas.batch.getShader.getFragmentShaderSource)

      case Render(canvas) =>

        val monospaceFont = app.resource[Font]("font:monospace-default")

        canvas.setOrtho(
          yDown = false,
          width = canvas.width,
          height = canvas.height
        )
        canvas.drawFrame(
          background = Color.GRAY,
          camPos = Vec2(
            x = canvas.width / 2.0f + Random.nextFloat() * 5.0f,
            y = canvas.height / 2.0f + Random.nextFloat() * 5.0f
          )) {

          canvas.drawString(
            char = "A",
            font = monospaceFont,
            color = Color.GREEN,
            at = Vec2(140, 140),
            rotate = 45,
            scale = 50
          )

          canvas.drawString(
            char = "B",
            font = monospaceFont,
            color = Color.GREEN,
            at = Vec2(240, 240),
            rotate = -45,
            scale = 50
          )

          canvas.drawString(
            char = "CDEFG",
            font = monospaceFont,
            color = Color.GREEN,
            at = Vec2(400, 400),
            rotate = 180 + app.time.toFloat * 360.0f,
            scale = 50
          )

        }
      case event => // mouse, kb, resize, ..
      //   println(event)
    }

  }
}

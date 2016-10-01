package se.gigurra.glasciia

import ApplicationEvent._
import com.badlogic.gdx.graphics.Color
import se.gigurra.glasciia.conf.{CameraConf, GlConf, ScaleType, WindowConf}
import se.gigurra.glasciia.impl.{ApplicationEventListener, LwjglImplementation, ResourceManager}
import se.gigurra.math.Vec2

/**
  * Created by johan on 2016-09-26.
  */
object OpenWindowTest {

  def main(args: Array[String]): Unit = {

    val initialWindowConf = WindowConf(
      position = Vec2(100,100),
      size = Vec2(640, 480),
      resizable = false,
      maximized = false,
      title = "Test Window"
    )

    val initialCameraConf = CameraConf(
      pos = Vec2(0.0f, 0.0f),
      size = 2.0f,
      scaleType = ScaleType.Conformal
    )

    val initialGlConf = GlConf(
      vsync = true,
      msaa = 4,
      foregroundFpsCap = None,
      backgroundFpsCap = Some(30)
    )

    val app =
      new App(
        initialWindowConf = initialWindowConf,
        initialCameraConf = initialCameraConf,
        initialGlConf = initialGlConf
      ) with ApplicationEventListener
        with ResourceManager
        with LwjglImplementation

    app.storeResource[Font]("font:monospace-default", Font.fromTtfFile("pt-mono/PTM55FT.ttf"), _.close())

    app.handleEvents {
      case Render(canvas) =>

        val font = app.resource[Font]("font:monospace-default")

        canvas.setOrtho(yDown = false, width = canvas.width, height = canvas.height)
        canvas.drawFrame(background = Color.GRAY) {


        }
      case event => // mouse, kb, resize, ..
         println(event)
    }

  }
}

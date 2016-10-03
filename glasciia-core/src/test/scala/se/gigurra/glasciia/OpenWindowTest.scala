package se.gigurra.glasciia

import java.io.FileNotFoundException
import java.time.Duration

import ApplicationEvent._
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.scenes.scene2d.Stage
import se.gigurra.glasciia.conf.{GlConf, WindowConf}
import se.gigurra.glasciia.impl.TextDrawer.Anchor
import se.gigurra.glasciia.impl._
import se.gigurra.glasciia.util.LoadFile
import se.gigurra.math.Vec2

import scala.util.Random
import Glasciia._
import com.badlogic.gdx.math.Vector3

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

    app.addResource("font:monospace-default", Font.fromTtfFile("pt-mono/PTM55FT.ttf"))
    app.addResource("gui:main-menu", new Stage())
    app.addResource("gui:main-menu:visible", true)

    app.addResource("animation:capguy-walk", Animation("animations/capguy-walk.png", nx = 8, ny = 1, dt = Duration.ofMillis(100), mode = PlayMode.LOOP))
    app.addResource("animation:capguy-walk:instance-0", app.resource[Animation]("animation:capguy-walk").newInstance())
    app.addResource("image:test-image", StaticImage("images/test-image.png"))

    app.addResource("particle-effect:test-effect:instance-0", Particles.standardEffect(
      effectFile = LoadFile("particle-effects/test-effect.party").getOrElse(throw new FileNotFoundException(s"Could not find test particle effect")),
      imagesDir = LoadFile("").get
    ))

    app.handleEvents {

      case Init(canvas) =>

        println()
        println("VERTEX SHADER SOURCE")
        println("--------------------")
        println(canvas.batch.getShader.getVertexShaderSource)
        println()
        println("FRAGMENT SHADER SOURCE")
        println("----------------------")
        println(canvas.batch.getShader.getFragmentShaderSource)
        val effect1 = app.resource[ParticleEffect]("particle-effect:test-effect:instance-0")
        effect1.scaleEffect(0.5f)
        effect1.start()
        val effect2 = effect1.copy
        effect2.scaleEffect(0.5f)
        effect2.flipY()
        effect2.start()
        app.addResource("particle-effect:test-effect:instance-1", effect2)
        app.addResource[Vec2[Float]]("camera-position", Vec2(
          x = canvas.width / 2 + Random.nextFloat() * 5.0f,
          y = canvas.height / 2 + Random.nextFloat() * 5.0f
        ))

      case Render(canvas) =>

        val speed = 100.0f
        val dr = Pov4W().dir.toFloat * Gdx.graphics.getDeltaTime * speed
        val prevCameraPos = app.resource[Vec2[Float]]("camera-position")
        app.addResource("camera-position", Vec2[Float](prevCameraPos.x + dr.x, prevCameraPos.y + dr.y))

        val monospaceFont = app.resource[Font]("font:monospace-default")
        val walkingDudeAnimation = app.resource[Animation.Instance]("animation:capguy-walk:instance-0")
        val testImage = app.resource[StaticImage]("image:test-image")
        val effect1 = app.resource[ParticleEffect]("particle-effect:test-effect:instance-0")
        val effect2 = app.resource[ParticleEffect]("particle-effect:test-effect:instance-1")
        val cameraPos = app.resource[Vec2[Float]]("camera-position")

        canvas.setOrtho(
          yDown = false,
          width = canvas.width,
          height = canvas.height
        )
        canvas.drawFrame(
          background = Color.DARK_GRAY,
          camPos = cameraPos) {

          canvas.drawText(
            text = "A",
            font = monospaceFont,
            color = Color.GREEN,
            at = Vec2(140, 140),
            rotate = 45,
            scale = 50
          )

          canvas.drawText(
            text = "B",
            font = monospaceFont,
            color = Color.GREEN,
            at = Vec2(240, 240),
            rotate = -45,
            scale = 50
          )

          canvas.drawText(
            text = "CDEFG",
            font = monospaceFont,
            color = Color.GREEN,
            at = Vec2(400, 400),
            rotate = 180 + app.timeSinceStart.toMillis * 0.360f,
            scale = 50
          )

          canvas.drawText(
            text = "UL",
            font = monospaceFont,
            color = Color.RED,
            at = Vec2(0, canvas.height),
            scale = 50
          )

          canvas.drawText(
            text = "LL",
            font = monospaceFont,
            color = Color.GREEN,
            at = Vec2(0, 50),
            scale = 50
          )

          canvas.drawText(
            text = "UR",
            font = monospaceFont,
            color = Color.BLUE,
            at = Vec2(canvas.width - 50 * 2 * monospaceFont.spaceWidth(), canvas.height),
            scale = 50
          )

          canvas.drawText(
            text = "LR",
            font = monospaceFont,
            color = Color.BLACK,
            at = Vec2(canvas.width - 50 * 2 * monospaceFont.spaceWidth(), 50),
            scale = 50
          )

          canvas.drawAnimation(
            animation = walkingDudeAnimation,
            at = Vec2(400, 100),
            scale = Vec2(120.0f, 200.0f)
          )

          canvas.drawStaticImage(
            image = testImage,
            at = Vec2(100, 300),
            scale = Vec2(120.0f, 120.0f)
          )

          canvas.drawEffect(
            effect = effect1,
            at = testEffectPosition(canvas)
          )

          canvas.drawEffect(
            effect = effect2,
            at = testEffectPosition(canvas)
          )

          canvas.drawText(
            text = "CAM",
            font = monospaceFont,
            color = Color.WHITE,
            anchor = Anchor.CC,
            scale = 25,
            at = cameraPos
          )

        }
      case input: InputEvent =>

        val mainMenuGui = app.resource[Stage]("gui:main-menu")
        val mainMenuVisible = app.resource[Boolean]("gui:main-menu:visible")

        input
          .filterIf(mainMenuVisible, mainMenuGui)
          .filter {
            case event: KeyboardEvent =>
              println(s"Input event propageted to world/Not consumed by gui: $event")
          }
    }

  }

  private def testEffectPosition(canvas: Canvas): Vec2[Float] = {
    val t = canvas.app.timeSinceStart.toMillis
    val step = 0.05f
    val n = 5000
    Vec2(canvas.width / 2.0f + (t % n) * step, canvas.height / 2.0f)
  }
}

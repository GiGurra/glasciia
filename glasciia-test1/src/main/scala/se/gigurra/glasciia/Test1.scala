package se.gigurra.glasciia

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.badlogic.gdx.graphics.{Color, Cursor}
import com.badlogic.gdx.scenes.scene2d.Stage
import se.gigurra.glasciia.App.{GlConf, WindowConf}
import se.gigurra.glasciia.AppEvent._
import se.gigurra.glasciia.Glasciia._
import se.gigurra.glasciia.impl.TextDrawer.Anchor
import se.gigurra.glasciia.impl._
import se.gigurra.math.{Box2, Vec2}

import scala.util.Random

/**
  * Created by johan on 2016-09-26.
  */
object Test1 {

  def main(args: Array[String]): Unit = {

    val initialWindowConf = WindowConf(
      position = Vec2(100, 100),
      size = Vec2(640, 480),
      resizable = true,
      maximized = false,
      fullscreen = false,
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

    // UNCOMMENT TO TEST TEXTURE ATLASING
  //  val texturePackSettings = readTexturePackSettings("test-atlast-cfg.json")
  //  val inputFolder = new File(getClass.getClassLoader.getResource("test-atlast-cfg.json").getFile).getAbsoluteFile.getParent
   // val outputFolder = LoadFile("target/").get.file().getAbsolutePath

   // packFilesIntoTextureAtlas(texturePackSettings, inputDir = inputFolder, outputDir = outputFolder, "test-atlast.atlas")

   // println(s"$inputFolder -> $outputFolder")

   // app.addResource("texture-atlas", new TextureAtlas(s"$outputFolder/test-atlast.atlas", outputFolder))

    app.addResource("font:monospace-default", Font.fromTtfFile("pt-mono/PTM55FT.ttf"))
    app.addResource("font:monospace-default-masked", app.resource[Font]("font:monospace-default").createMaskedInstance(maskChar = Font.DEFAULT_MASK_CHAR, deleteSource = false))
    app.addResource("gui:main-menu", new Stage())
    app.addResource("gui:main-menu:visible", true)

    app.addResource("animation:capguy-walk", Animation.fromFile("animations/capguy-walk.png", nx = 8, ny = 1, dt = 0.1, mode = PlayMode.LOOP))
    app.addResource("animation:capguy-walk:instance-0", app.resource[Animation]("animation:capguy-walk").newInstance(t0 = app.localAppTime))
    app.addResource("image:test-image", StaticImage.fromFile("images/test-image.png"))

    app.addResource("particle-effect:test-effect:instance-0", Particles.standard("particle-effects/test-effect.party", "particle-effects/"))
    app.addResource("bg-image", StaticImage.fromFile("backgrounds/bgtest2.jpg"))

    app.addResource("background-0",
      MultiLayer[Image]() {
        _.layer(translationScale = 0.5f, camZero = Vec2(320.0f, 240.0f)) {
          _.piece(
            bounds = Box2(ll = Vec2(0.0f, 0.0f), size = Vec2(640.0f, 480.0f)),
            image = app.resource[Image]("bg-image")
          )
        }
        .layer(translationScale = 0.75f, camZero = Vec2(320.0f, 240.0f)) {
          _.piece(
            bounds = Box2(ll = Vec2(120.0f, 200.0f), size = Vec2(40.0f, 80.0f)),
            image = app.resource[Image]("bg-image")
          ).piece(
            bounds = Box2(ll = Vec2(240.0f, 200.0f), size = Vec2(40.0f, 80.0f)),
            image = app.resource[Image]("bg-image")
          ).piece(
            bounds = Box2(ll = Vec2(360.0f, 200.0f), size = Vec2(40.0f, 80.0f)),
            image = app.resource[Image]("bg-image")
          )
        }
      }
    )

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
        val effect1 = app.resource[ParticleSource]("particle-effect:test-effect:instance-0").scaleEffect(0.5f)
        app.addResource("particle-effect:test-effect:instance-1", effect1.copy.scaleEffect(0.5f).flipY())
        app.addResource("particle-effect:test-effect:instance-2", effect1.copy.scaleEffect(0.25f))
        app.addResource[Vec2[Float]]("camera-position", Vec2(
          x = canvas.width / 2 + Random.nextFloat() * 5.0f,
          y = canvas.height / 2 + Random.nextFloat() * 5.0f
        ))

        app.addResource("cool-cursor", canvas.createCursor("cursors/c2.png"))

        canvas.setCursor(app.resource[Cursor]("cool-cursor"))


      case Render(canvas) =>

        val speed = 100.0f
        val dr = Pov4W().dir.toFloat * Gdx.graphics.getDeltaTime * speed
        val prevCameraPos = app.resource[Vec2[Float]]("camera-position")
        app.addResource("camera-position", prevCameraPos + dr)

        val monospaceFont = app.resource[Font]("font:monospace-default")
        val monospaceFontMasked = app.resource[Font]("font:monospace-default-masked")
        val walkingDudeAnimation = app.resource[Animation.Instance]("animation:capguy-walk:instance-0")
        val testImage = app.resource[StaticImage]("image:test-image")
        val effect1 = app.resource[ParticleSource]("particle-effect:test-effect:instance-0")
        val effect2 = app.resource[ParticleSource]("particle-effect:test-effect:instance-1")
        val effect3 = app.resource[ParticleSource]("particle-effect:test-effect:instance-2")

        val cameraPos = app.resource[Vec2[Float]]("camera-position")
        val mouseWorldPos = canvas.screen2World(canvas.mousePos)

        val background = app.resource[MultiLayer[Image]]("background-0")

        canvas.setOrtho(
          yDown = false,
          width = 480 * canvas.aspectRatio,
          height = 480
        )

        canvas.drawFrame(
          background = Color.DARK_GRAY,
          camPos = cameraPos) {

          canvas.drawBackGround(background)

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
            rotate = 180 + canvas.drawTime.toFloat * 360.0f,
            scale = 50
          )

          canvas.drawText(
            text = "UL",
            font = monospaceFont,
            color = Color.RED,
            at = Vec2(0, 480),
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
            at = Vec2(640 - 50 * 2 * monospaceFont.spaceWidth(), 480),
            scale = 50
          )

          canvas.drawText(
            text = "LR",
            font = monospaceFont,
            color = Color.BLACK,
            at = Vec2(640 - 50 * 2 * monospaceFont.spaceWidth(), 50),
            scale = 50
          )

          canvas.drawAnimation(
            animation = walkingDudeAnimation,
            at = Vec2(400, 100),
            scale = Vec2(120.0f, 200.0f)
          )

          canvas.drawImage(
            image = testImage,
            at = Vec2(100, 300),
            scale = Vec2(120.0f, 120.0f)
          )

          canvas.drawParticles(
            effect = effect1,
            at = testEffectPosition(canvas.drawTime)
          )

          canvas.drawParticles(
            effect = effect2,
            at = testEffectPosition(canvas.drawTime)
          )

          canvas.drawParticles(
            effect = effect3,
            at = mouseWorldPos,
            angle = canvas.drawTime.toFloat * 180.0f
          )

          canvas.drawText(
            text = "CAM",
            font = monospaceFont,
            color = Color.WHITE,
            anchor = Anchor.CC,
            scale = 25,
            at = cameraPos
          )

          canvas.drawText(
            text = "CAM",
            font = monospaceFontMasked,
            color = Color.WHITE,
            anchor = Anchor.CC,
            scale = 25,
            at = cameraPos - Vec2(0.0f, 50.0f)
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

  private def testEffectPosition(tSec: Double): Vec2[Float] = {
    Vec2[Float](320 + ((tSec * 1000.0).toLong % 5000L) * 0.05f, 240 / 2.0f)
  }
}

package se.gigurra.glasciia.test1.testcomponents

import java.io.File

import com.badlogic.gdx.graphics.{Color, Pixmap, Texture}
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.{Image => Scene2dImage}
import com.badlogic.gdx.scenes.scene2d.ui.{Skin, Table, TextButton}
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import se.gigurra.glasciia.Glasciia._
import se.gigurra.glasciia._
import se.gigurra.glasciia.impl.LoadFile
import se.gigurra.math.{Box2, Vec2}

/**
  * Created by johan on 2016-10-08.
  */
object loadResources {

  def apply(app: App): Unit = app.executeOnRenderThread {
    // UNCOMMENT TO TEST TEXTURE ATLASING
    // loadTextureAtlases(app)
    loadFonts(app)
    loadImages(app)
    loadParticleEffects(app)
    loadBackground(app)
    loadGui(app)
  }

  private def loadTextureAtlases(app: App): Unit = {
    val texturePackSettings = readTexturePackSettings("test-atlast-cfg.json")
    val inputFolder = new File(getClass.getClassLoader.getResource("test-atlast-cfg.json").getFile).getAbsoluteFile.getParent
    val outputFolder = LoadFile("target/").get.file().getAbsolutePath
    packFilesIntoTextureAtlas(texturePackSettings, inputDir = inputFolder, outputDir = outputFolder, "test-atlast.atlas")
    println(s"$inputFolder -> $outputFolder")
    app.addResource("texture-atlas", new TextureAtlas(s"$outputFolder/test-atlast.atlas", outputFolder))
  }

  private def loadFonts(app: App): Unit = {
    app.addResource("font:monospace-default", Font.fromTtfFile("pt-mono/PTM55FT.ttf"))
    app.addResource("font:monospace-default-masked", app.resource[Font]("font:monospace-default").createMaskedInstance(maskChar = Font.DEFAULT_MASK_CHAR, deleteSource = false))
  }

  private def loadImages(app: App): Unit = {
    app.addResource("animation:capguy-walk", Animation.fromFile("animations/capguy-walk.png", nx = 8, ny = 1, dt = 0.1, mode = PlayMode.LOOP))
    app.addResource("animation:capguy-walk:instance-0", app.resource[Animation]("animation:capguy-walk").newInstance(t0 = app.localAppTime))
    app.addResource("image:test-image", StaticImage.fromFile("images/test-image.png"))
  }

  private def loadParticleEffects(app: App): Unit = {
    app.addResource("particle-effect:test-effect:instance-0", Particles.standard("particle-effects/test-effect.party", "particle-effects/").scaleEffect(0.5f))
    def effect0 = app.resource[ParticleSource]("particle-effect:test-effect:instance-0")
    app.addResource("particle-effect:test-effect:instance-1", effect0.copy.scaleEffect(0.5f).flipY().setTint(Color.TEAL))
    app.addResource("particle-effect:test-effect:instance-2", effect0.copy.scaleEffect(0.25f))
    app.addResource("camera-position", Vec2(x = 320.0f, y = 240.0f))
    app.addResource("cool-cursor", createCursor("cursors/c2.png"))
  }

  private def loadBackground(app: App): Unit = {
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
  }

  private def loadGui(app: App): Unit = {
    app.addResource("gui:main-menu", Gui())
    app.addResource("gui:main-menu:font", app.resource[Font]("font:monospace-default"))
    app.addResource("gui:main-menu:font-masked", app.resource[Font]("font:monospace-default-masked"))

    val mainMenuGui = app.resource[Gui]("gui:main-menu")
    val mainMenuSkin = new Skin()

    val fillPixMap = new Pixmap(1, 1, Pixmap.Format.RGBA8888)
    fillPixMap.setColor(Color.WHITE)
    fillPixMap.fill()
    mainMenuSkin.add("fill", new Texture(fillPixMap))
    mainMenuSkin.add("default-font", app.resource[Font]("gui:main-menu:font").font)
    mainMenuSkin.add("masked-font", app.resource[Font]("gui:main-menu:font-masked").font)

    val mainMenuButtonStyle = new TextButtonStyle
    mainMenuButtonStyle.up = mainMenuSkin.newDrawable("fill", Color.DARK_GRAY)
    mainMenuButtonStyle.down = mainMenuSkin.newDrawable("fill", Color.DARK_GRAY)
    mainMenuButtonStyle.checked = mainMenuSkin.newDrawable("fill", Color.BLUE)
    mainMenuButtonStyle.over = mainMenuSkin.newDrawable("fill", Color.LIGHT_GRAY)
    mainMenuButtonStyle.font = mainMenuSkin.getFont("default-font")

    mainMenuSkin.add("main-menu:button-style", mainMenuButtonStyle)

    // Create a table that fills the screen. Everything else will go inside this table.
    val mainMenuGuiTable = new Table()
    mainMenuGuiTable.setFillParent(true)

    // Create a button. A 3rd parameter can be used to specify a name other than "default".
    val button = new TextButton("Click me!", mainMenuSkin, "main-menu:button-style")
    mainMenuGuiTable.add(button)

    mainMenuGuiTable.add(new Scene2dImage(mainMenuSkin.newDrawable("fill", Color.RED))).size(64)


    // Add everything to the stage
    mainMenuGui.addActor(mainMenuGuiTable)
  }


}

package se.gigurra.glasciia.test1.testcomponents

import com.badlogic.gdx.graphics.{Color, Pixmap, Texture}
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.{Image => Scene2dImage}
import com.badlogic.gdx.scenes.scene2d.ui.{Table, TextButton}
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import se.gigurra.glasciia.Glasciia._
import se.gigurra.glasciia._
import se.gigurra.math.{Box2, Vec2}

/**
  * Created by johan on 2016-10-08.
  */
object loadResources {

  def apply(app: App): Unit = app.executeOnRenderThread {

    app.addResource("texture-loader", TextureRegionLoader.createNew())
    val regionLoader = app.resource[Loader.InMemory[TextureRegion]]("texture-loader")

    loadFonts(app, regionLoader)
    loadImages(app, regionLoader)
    loadParticleEffects(app, regionLoader)
    loadBackground(app, regionLoader)
    loadGui(app, regionLoader)
  }

  private def loadFonts(app: App, regionLoader: Loader[TextureRegion]): Unit = {
    app.addResource("font:monospace-default", Font.fromTtfFile("pt-mono/PTM55FT.ttf"))
    app.addResource("font:monospace-default-masked", app.resource[Font]("font:monospace-default").createMaskedInstance(maskChar = Font.DEFAULT_MASK_CHAR, deleteSource = false))
  }

  private def loadImages(app: App, regionLoader: Loader[TextureRegion]): Unit = {
    app.addResource("animation:capguy-walk", Animation(regionLoader("animations/capguy-walk.png"), nx = 8, ny = 1, dt = 0.1, mode = PlayMode.LOOP))
    app.addResource("animation:capguy-walk:instance-0", app.resource[Animation]("animation:capguy-walk").newInstance(t0 = app.localAppTime))
    app.addResource("image:test-image", StaticImage(regionLoader("images/test-image.png")))
  }

  private def loadParticleEffects(app: App, regionLoader: Loader[TextureRegion]): Unit = {
    app.addResource("particle-effect:test-effect:instance-0", Particles.standard("particle-effects/test-effect.party", "particle-effects/").scaleEffect(0.5f))
    def effect0 = app.resource[ParticleSource]("particle-effect:test-effect:instance-0")
    app.addResource("particle-effect:test-effect:instance-1", effect0.copy.scaleEffect(0.5f).flipY().setTint(Color.TEAL))
    app.addResource("particle-effect:test-effect:instance-2", effect0.copy.scaleEffect(0.25f))
    app.addResource("camera-position", Vec2(x = 320.0f, y = 240.0f))
    app.addResource("cool-cursor", createCursor("cursors/c2.png"))
  }

  private def loadBackground(app: App, regionLoader: Loader[TextureRegion]): Unit = {
    app.addResource("bg-image", StaticImage(regionLoader("backgrounds/bgtest2.jpg")))
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

  private def loadGui(app: App, regionLoader: Loader.InMemory[TextureRegion]): Unit = {
    app.addResource("gui:main-menu", Gui())
    app.addResource("gui:main-menu:font", app.resource[Font]("font:monospace-default"))
    app.addResource("gui:main-menu:font-masked", app.resource[Font]("font:monospace-default-masked"))

    val mainMenu = app.resource[Gui]("gui:main-menu")
    regionLoader.add("fill-texture", {
      val fillPixMap = new Pixmap(1, 1, Pixmap.Format.RGBA8888)
      fillPixMap.setColor(Color.WHITE)
      fillPixMap.fill()
      StaticImage.fromPixMap(fillPixMap)
    })

    mainMenu.skin.add("fill", regionLoader("fill-texture"))
    mainMenu.skin.add("default-font", app.resource[Font]("gui:main-menu:font").font)
    mainMenu.skin.add("masked-font", app.resource[Font]("gui:main-menu:font-masked").font)

    val mainMenuButtonStyle = new TextButtonStyle
    mainMenuButtonStyle.up = mainMenu.skin.newDrawable("fill", Color.DARK_GRAY)
    mainMenuButtonStyle.down = mainMenu.skin.newDrawable("fill", Color.DARK_GRAY)
    mainMenuButtonStyle.checked = mainMenu.skin.newDrawable("fill", Color.BLUE)
    mainMenuButtonStyle.over = mainMenu.skin.newDrawable("fill", Color.LIGHT_GRAY)
    mainMenuButtonStyle.font = mainMenu.skin.getFont("default-font")
    mainMenu.skin.add("main-menu:button-style", mainMenuButtonStyle)

    mainMenu.table.add(new TextButton("Click me!", mainMenu.skin, "main-menu:button-style"))
    mainMenu.table.add(new Scene2dImage(mainMenu.skin.newDrawable("fill", Color.RED))).size(64)
  }


  /*
  private def loadTextureAtlases(app: App): Unit = {
    val texturePackSettings = readTexturePackSettings("test-atlast-cfg.json")
    val inputFolder = new File(getClass.getClassLoader.getResource("test-atlast-cfg.json").getFile).getAbsoluteFile.getParent
    val outputFolder = LoadFile("target/").get.file().getAbsolutePath
    packFilesIntoTextureAtlas(texturePackSettings, inputDir = inputFolder, outputDir = outputFolder, "test-atlast.atlas")
    println(s"$inputFolder -> $outputFolder")
    app.addResource("texture-atlas", new TextureAtlas(s"$outputFolder/test-atlast.atlas", outputFolder))
  }
  */

}

package com.github.gigurra.glasciia.test1.testcomponents

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.badlogic.gdx.graphics.g2d.{Batch, BitmapFont, TextureRegion}
import com.badlogic.gdx.graphics.{Color, Cursor, Pixmap, Texture}
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.gigurra.glasciia.Glasciia._
import com.github.gigurra.glasciia._
import com.github.gigurra.math.{Box2, Vec2}

/**
  * Created by johan on 2016-10-08.
  */
object loadResources {

  def apply(app: App): Unit = {
    val batch = app.canvas.batch
    val regions = createTextureRegionLoader(app)
    loadFonts(app, regions)
    loadImages(app, regions)
    loadParticleEffects(app, regions)
    loadBackground(app, regions)
    loadGui(app, batch, regions)
    loadCursor(app, regions)
    loadMipMaps(app, regions)

    println()
    println("Loaded Resources:")
    for (resource <- app.listResources.sortBy(_.path)) {
      println(s"  $resource")
    }
  }

  private def loadFonts(app: App, regions: Loader[TextureRegion]): Unit = {
    app.addResource("font:monospace-default", Font.fromTtfFile("pt-mono/PTM55FT.ttf"))
    app.addResource("font:monospace-default-masked", app.resource[BitmapFont]("font:monospace-default").createMaskedInstance(maskChar = Font.DEFAULT_MASK_CHAR, deleteSource = false))
  }

  private def loadImages(app: App, regions: Loader[TextureRegion]): Unit = {
    app.addResource("animation:capguy-walk", Animation(regions("animations/capguy-walk.png"), nx = 8, ny = 1, dt = 0.1, mode = PlayMode.LOOP))
    app.addResource("animation:capguy-walk:instance-0", app.resource[Animation]("animation:capguy-walk").newInstance(t0 = app.localAppTime))
    app.addResource("image:test-image", regions("images/test-image.png"))
    app.addResource("image:fill-yellow", regions("square-90-percent"))
  }

  private def loadParticleEffects(app: App, regions: Loader[TextureRegion]): Unit = {
    val effect0 = Particles.standard("particle-effects/test-effect.party", "particle-effects/").scaleEffect(0.5f)
    app.addResource("particle-effect:test-effect:instance-0", effect0)
    app.addResource("particle-effect:test-effect:instance-1", effect0.copy.scaleEffect(0.5f).flipY().setTint(Color.TEAL))
    app.addResource("particle-effect:test-effect:instance-2", effect0.copy.scaleEffect(0.25f))
    app.addResource("cool-cursor", createCursor("cursors/c2.png"))
  }

  private def loadBackground(app: App, regions: Loader[TextureRegion]): Unit = {
    app.addResource[TextureRegion]("bg-image", regions("backgrounds/bgtest2.jpg"))
    app.addResource("background-0",
      MultiLayer[TextureRegion]() {
        _.layer(translationScale = 0.5f, camZero = Vec2(320.0f, 240.0f)) {
          _.piece(
            bounds = Box2(ll = Vec2(0.0f, 0.0f), size = Vec2(640.0f, 480.0f)),
            image = app.resource[TextureRegion]("bg-image")
          )
        }.layer(translationScale = 0.75f, camZero = Vec2(320.0f, 240.0f)) {
          _.piece(
            bounds = Box2(ll = Vec2(120.0f, 200.0f), size = Vec2(40.0f, 80.0f)),
            image = app.resource[TextureRegion]("bg-image")
          ).piece(
            bounds = Box2(ll = Vec2(240.0f, 200.0f), size = Vec2(40.0f, 80.0f)),
            image = app.resource[TextureRegion]("bg-image")
          ).piece(
            bounds = Box2(ll = Vec2(360.0f, 200.0f), size = Vec2(40.0f, 80.0f)),
            image = app.resource[TextureRegion]("bg-image")
          )
        }
      }
    )
  }

  private def loadGui(app: App, batch: Batch, regions: InMemoryLoader[TextureRegion]): Unit = {
    app.addResource[Stage]("gui:main-menu", createMainMenu(app, batch, regions))
    app.addResource[Stage]("gui:game-world", createGameWorldGui(app, batch, regions))
  }

  private def createTextureRegionLoader(app: App): InMemoryLoader[TextureRegion] = {
    val out = TextureRegionLoader.newDefault()()
    app.addResource("texture-loader", out)
    out.add("filled-texture", {
      val fillPixMap = new Pixmap(1, 1, Pixmap.Format.RGBA8888)
      fillPixMap.setColor(Color.WHITE)
      fillPixMap.fill()
      StaticImage.fromPixMap(fillPixMap)
    })
    out.add("square-90-percent", {
      val out = new Pixmap(400, 400, Pixmap.Format.RGBA8888)
      out.setColor(Color.YELLOW.scaleAlpha(0.9f))
      out.fill()
      Pixmap.setBlending(Pixmap.Blending.None)
      out.setColor(new Color(0,0,0,0))
      out.fillRectangle(10,10,380,380)
      Pixmap.setBlending(Pixmap.Blending.SourceOver)
      StaticImage.fromPixMap(out)
    })
    out
  }

  private def loadCursor(app: App, regions: Loader[TextureRegion]): Unit = {
    app.canvas.setCursor(app.resource[Cursor]("cool-cursor"))
  }

  private def loadMipMaps(app: App, regions: InMemoryLoader[TextureRegion]): Unit = {
    regions.uploadIfDirty()
  }
}

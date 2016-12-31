package com.github.gigurra.glasciia.test1.testcomponents

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.badlogic.gdx.graphics.g2d.{BitmapFont, TextureRegion}
import com.badlogic.gdx.graphics.{Color, Cursor, Pixmap}
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.gigurra.glasciia.Glasciia._
import com.github.gigurra.glasciia.{ResourceManager, _}
import com.github.gigurra.math.{Box2, Vec2}

/**
  * Created by johan on 2016-10-08.
  */
object loadResources extends Logging {

  def apply(game: Game): ResourceManager = {
    val resources = new ResourceManager
    val regions = createTextureRegionLoader(resources)
    loadFonts(resources, regions)
    loadImages(resources, regions)
    loadParticleEffects(resources, regions)
    loadBackground(resources, regions)
    loadGui(game, resources, regions)
    loadCursor(resources, regions)
    loadMipMaps(regions)

    log.info("Loaded Resources:")
    for (resource <- resources.listResources.sortBy(_.path.toString)) {
      log.info(s"  $resource")
    }
    resources
  }

  private def loadFonts(resources: ResourceManager, regions: Loader[TextureRegion]): Unit = {
    resources.add("font:monospace-default", Font.fromTtfFile("pt-mono/PTM55FT.ttf"))
    resources.add("font:monospace-default-masked", resources[BitmapFont]("font:monospace-default").createMaskedInstance(maskChar = Font.DEFAULT_MASK_CHAR, deleteSource = false))
  }

  private def loadImages(resources: ResourceManager, regions: Loader[TextureRegion]): Unit = {
    resources.add("animation:capguy-walk", Animation(regions("animations/capguy-walk.png"), nx = 8, ny = 1, dt = 100L, mode = PlayMode.LOOP))
    resources.add("animation:capguy-walk:instance-0", resources[Animation]("animation:capguy-walk").newInstance(t0 = 0L))
    resources.add("image:test-image", regions("images/test-image.png"))
    resources.add("image:fill-yellow", regions("square-90-percent"))
  }

  private def loadParticleEffects(resources: ResourceManager, regions: Loader[TextureRegion]): Unit = {
    val effect0 = Particles.standard("particle-effects/test-effect.party", "particle-effects/").scaleEffect(0.5f)
    resources.add("particle-effect:test-effect:instance-0", effect0)
    resources.add("particle-effect:test-effect:instance-1", effect0.copy.scaleEffect(0.5f).flipY().setTint(Color.TEAL))
    resources.add("particle-effect:test-effect:instance-2", effect0.copy.scaleEffect(0.25f))
    resources.add("cool-cursor", createCursor("cursors/c2.png"))
  }

  private def loadBackground(resources: ResourceManager, regions: Loader[TextureRegion]): Unit = {
    resources.add[TextureRegion]("bg-image", regions("backgrounds/bgtest2.jpg"))
    resources.add("background-0",
      MultiLayer[TextureRegion]() {
        _.layer(translationScale = 0.5f, camZero = Vec2(320.0f, 240.0f)) {
          _.piece(
            bounds = Box2(ll = Vec2(0.0f, 0.0f), size = Vec2(640.0f, 480.0f)),
            image = resources[TextureRegion]("bg-image")
          )
        }.layer(translationScale = 0.75f, camZero = Vec2(320.0f, 240.0f)) {
          _.piece(
            bounds = Box2(ll = Vec2(120.0f, 200.0f), size = Vec2(40.0f, 80.0f)),
            image = resources[TextureRegion]("bg-image")
          ).piece(
            bounds = Box2(ll = Vec2(240.0f, 200.0f), size = Vec2(40.0f, 80.0f)),
            image = resources[TextureRegion]("bg-image")
          ).piece(
            bounds = Box2(ll = Vec2(360.0f, 200.0f), size = Vec2(40.0f, 80.0f)),
            image = resources[TextureRegion]("bg-image")
          )
        }
      }
    )
  }

  private def loadGui(game: Game, resources: ResourceManager, regions: InMemoryLoader[TextureRegion]): Unit = {
    resources.add[Stage]("gui:main-menu", createMainMenu(game, resources, regions))
    resources.add[Stage]("gui:game-world", createGameWorldGui(resources, regions))
  }

  private def createTextureRegionLoader(resources: ResourceManager): InMemoryLoader[TextureRegion] = {
    val out = TextureRegionLoader.newDefault()()
    resources.add("texture-loader", out)
    out.add("filled-texture", {
      val fillPixMap = new Pixmap(1, 1, Pixmap.Format.RGBA8888)
      fillPixMap.setColor(Color.WHITE)
      fillPixMap.fill()
      StaticImage.fromPixMap(fillPixMap)
    })
    out.add("circle-texture", {
      val fillPixMap = new Pixmap(101, 101, Pixmap.Format.RGBA8888)
      Pixmap.setBlending(Pixmap.Blending.None)
      fillPixMap.setColor(Color.WHITE.scaleAlpha(0.0f))
      fillPixMap.fill()
      fillPixMap.setColor(Color.WHITE)
      fillPixMap.fillCircle(50, 50, 50)
      Pixmap.setBlending(Pixmap.Blending.SourceOver)
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

  private def loadCursor(resources: ResourceManager, regions: Loader[TextureRegion]): Unit = {
    Gdx.graphics.setCursor(resources[Cursor]("cool-cursor"))
  }

  private def loadMipMaps(regions: InMemoryLoader[TextureRegion]): Unit = {
    regions.uploadIfDirty()
  }
}

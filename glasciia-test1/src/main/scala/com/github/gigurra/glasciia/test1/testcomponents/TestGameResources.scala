package com.github.gigurra.glasciia.test1.testcomponents

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.badlogic.gdx.graphics.g2d.{BitmapFont, TextureRegion}
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.graphics._
import com.github.gigurra.glasciia.Glasciia._
import com.github.gigurra.glasciia.{ResourceManager, _}
import com.github.gigurra.math.{Box2, Vec2}

/**
  * Created by johan on 2016-10-08.
  */
class TestGameResources(canvas: Canvas) extends Resources with Logging {

  for {
    regions <- createTextureRegionLoader(resources)
    _       <- loadTestTextures(resources, regions)
    _       <- addLoadTask(loadTestTextures(resources, regions))
    _       <- addLoadTask(drawSomeFboCircle(resources, regions))
    _       <- addLoadTask(loadFonts(resources, regions))
    _       <- addLoadTask(loadImages(resources, regions))
    _       <- addLoadTask(loadParticleEffects(resources, regions))
    _       <- addLoadTask(loadBackground(resources, regions))
    _       <- addLoadTask(loadGui(resources, regions))
    _       <- addLoadTask(loadCursor(resources, regions))
    _       <- addLoadTask(loadMipMaps(regions))
    _       <- addLoadTask(printLoadedResults())
  } {
    this.add("texture-loader", regions)
  }

  def resources: TestGameResources = {
    this
  }

  private def printLoadedResults (): Unit ={
    log.info("Loaded Resources:")
    for (resource <- resources.listResources.sortBy(_.path.toString)) {
      log.info(s"  $resource")
    }
  }

  private def loadFonts(resources: ResourceManager, regions: Loader[TextureRegion]): Unit = {
    log.info("loadFonts")
    Thread.sleep(100)
    resources.add("font:monospace-default", Font.fromTtfFile("pt-mono/PTM55FT.ttf"))
    resources.add("font:monospace-default-masked", resources[BitmapFont]("font:monospace-default").createMaskedInstance(maskChar = Font.DEFAULT_MASK_CHAR, deleteSource = false))
  }

  private def loadImages(resources: ResourceManager, regions: Loader[TextureRegion]): Unit = {
    log.info("loadImages")
    Thread.sleep(100)
    resources.add("animation:capguy-walk", Animation(regions("animations/capguy-walk.png"), nx = 8, ny = 1, dt = 100L, mode = PlayMode.LOOP))
    resources.add("animation:capguy-walk:instance-0", resources[Animation]("animation:capguy-walk").newInstance(t0 = 0L))
    resources.add("image:test-image", regions("images/test-image.png"))
    resources.add("image:fill-yellow", regions("square-90-percent"))
  }

  private def loadParticleEffects(resources: ResourceManager, regions: Loader[TextureRegion]): Unit = {
    log.info("loadParticleEffects")
    Thread.sleep(100)
    val effect0 = Particles.standard("particle-effects/test-effect.party", "particle-effects/").scaleEffect(0.5f)
    resources.add("particle-effect:test-effect:instance-0", effect0)
    resources.add("particle-effect:test-effect:instance-1", effect0.copy.scaleEffect(0.5f).flipY().setTint(Color.TEAL))
    resources.add("particle-effect:test-effect:instance-2", effect0.copy.scaleEffect(0.25f))
    resources.add("cool-cursor", createCursor("cursors/c2.png"))
  }

  private def loadBackground(resources: ResourceManager, regions: Loader[TextureRegion]): Unit = {
    log.info("loadBackground")
    Thread.sleep(100)
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

  private def loadGui(resources: ResourceManager, regions: InMemoryLoader[TextureRegion]): Unit = {
    log.info("loadGui")
    Thread.sleep(100)
    resources.add[MainMenu]("gui:main-menu", new MainMenu(resources, regions, canvas.batch))
    resources.add[GameWorldGui]("gui:game-world", new GameWorldGui(resources, regions, canvas.batch))
  }

  private def createTextureRegionLoader(resources: ResourceManager) = addLoadTask {
    log.info("createTextureRegionLoader")
    Thread.sleep(100)
    TextureRegionLoader.newDefault()()
  }

  private def drawSomeFboCircle(resources: ResourceManager, loader: InMemoryLoader[TextureRegion]) = addLoadTask {
    log.info("drawSomeFboCircle")
    Thread.sleep(100)

    val atlasLoader = loader.impl.asInstanceOf[AtlasTextureRegionLoader]
    val reservedRegion = atlasLoader.reserve("fbo-circle", 1024, 1024)

    val fbo = TextureRegionFrameBuffer(reservedRegion, useDepth = false, useStencil = false)
    val camera = new OrthographicCamera(1024, 1024)
    val shapeRenderer = new ShapeRenderer()
    shapeRenderer.setProjectionMatrix(camera.combined)

    fbo.use {
      shapeRenderer.setColor(Color.GREEN)
      shapeRenderer.begin(ShapeType.Filled)
      shapeRenderer.circle(0.0f, 0.0f, 256.0f, 100)
      shapeRenderer.end()
    }
  }

  private def loadTestTextures(resources: ResourceManager, loader: InMemoryLoader[TextureRegion]) = addLoadTask {

    addLoadTask {
      log.info("loadTestTextures")
      Thread.sleep(100)
      resources.add("filled-texture", loader("images/filled-texture.png"))
      loader.add("filled-texture", loader("images/filled-texture.png"))
      resources.add("texture-loader", loader)
    }

    addLoadTask {
      loader.add("circle-texture", {
        val fillPixMap = new Pixmap(101, 101, Pixmap.Format.RGBA8888)
        Pixmap.setBlending(Pixmap.Blending.None)
        fillPixMap.setColor(Color.WHITE.scaleAlpha(0.0f))
        fillPixMap.fill()
        fillPixMap.setColor(Color.WHITE)
        fillPixMap.fillCircle(50, 50, 50)
        Pixmap.setBlending(Pixmap.Blending.SourceOver)
        StaticImage.fromPixMap(fillPixMap)
      })
    }

    addLoadTask {
      loader.add("square-90-percent", {
        val out = new Pixmap(400, 400, Pixmap.Format.RGBA8888)
        out.setColor(Color.YELLOW.scaleAlpha(0.9f))
        out.fill()
        Pixmap.setBlending(Pixmap.Blending.None)
        out.setColor(new Color(0, 0, 0, 0))
        out.fillRectangle(10, 10, 380, 380)
        Pixmap.setBlending(Pixmap.Blending.SourceOver)
        StaticImage.fromPixMap(out)
      })
    }
  }

  private def loadCursor(resources: ResourceManager, regions: Loader[TextureRegion]): Unit = {
    log.info("loadCursor")
    Thread.sleep(100)
    Gdx.graphics.setCursor(resources[Cursor]("cool-cursor"))
  }

  private def loadMipMaps(regions: InMemoryLoader[TextureRegion]): Unit = {
    log.info("loadMipMaps")
    Thread.sleep(100)
    regions.flush()
  }
}

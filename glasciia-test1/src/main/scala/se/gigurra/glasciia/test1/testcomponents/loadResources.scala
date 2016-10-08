package se.gigurra.glasciia.test1.testcomponents

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.badlogic.gdx.scenes.scene2d.Stage
import se.gigurra.glasciia.Glasciia._
import se.gigurra.glasciia._
import se.gigurra.math.{Box2, Vec2}

/**
  * Created by johan on 2016-10-08.
  */
object loadResources {
  def apply(app: App): Unit = app.executeOnRenderThread {

    // UNCOMMENT TO TEST TEXTURE ATLASING
    // testAtlases(app)

    app.addResource("font:monospace-default", Font.fromTtfFile("pt-mono/PTM55FT.ttf"))
    app.addResource("font:monospace-default-masked", app.resource[Font]("font:monospace-default").createMaskedInstance(maskChar = Font.DEFAULT_MASK_CHAR, deleteSource = false))
    app.addResource("gui:main-menu", new Stage())
    app.addResource("gui:main-menu:visible", true)

    app.addResource("animation:capguy-walk", Animation.fromFile("animations/capguy-walk.png", nx = 8, ny = 1, dt = 0.1, mode = PlayMode.LOOP))
    app.addResource("animation:capguy-walk:instance-0", app.resource[Animation]("animation:capguy-walk").newInstance(t0 = app.localAppTime))
    app.addResource("image:test-image", StaticImage.fromFile("images/test-image.png"))

    app.addResource("particle-effect:test-effect:instance-0", Particles.standard("particle-effects/test-effect.party", "particle-effects/").scaleEffect(0.5f))
    app.addResource("bg-image", StaticImage.fromFile("backgrounds/bgtest2.jpg"))
    def effect0 = app.resource[ParticleSource]("particle-effect:test-effect:instance-0")
    app.addResource("particle-effect:test-effect:instance-1", effect0.copy.scaleEffect(0.5f).flipY().setTint(Color.TEAL))
    app.addResource("particle-effect:test-effect:instance-2", effect0.copy.scaleEffect(0.25f))
    app.addResource("camera-position", Vec2(x = 320.0f, y = 240.0f))
    app.addResource("cool-cursor", createCursor("cursors/c2.png"))

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
}

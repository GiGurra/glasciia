package se.gigurra.glasciia.test1.testcomponents

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.{Color, Pixmap}
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.badlogic.gdx.graphics.g2d.{BitmapFont, TextureRegion}
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.{Label, TextButton}
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import se.gigurra.glasciia.AppEvent.KeyDown
import se.gigurra.glasciia.Glasciia._
import se.gigurra.glasciia._
import se.gigurra.math.{Box2, Vec2}

/**
  * Created by johan on 2016-10-08.
  */
object queLoadResources {

  private def createTextureRegionLoader(app: App): Loader.InMemory[TextureRegion] = {
    val out = TextureRegionLoader.createNew()
    app.addResource("texture-loader", out)
    out.add("filled-texture", {
      val fillPixMap = new Pixmap(1, 1, Pixmap.Format.RGBA8888)
      fillPixMap.setColor(Color.WHITE)
      fillPixMap.fill()
      StaticImage.fromPixMap(fillPixMap)
    })
    out
  }

  def apply(app: App): Unit = app.executeOnRenderThread {
    val regions = createTextureRegionLoader(app)
    loadHooks(app, regions)
    loadFonts(app, regions)
    loadImages(app, regions)
    loadParticleEffects(app, regions)
    loadBackground(app, regions)
    loadGui(app, regions)
  }

  private def loadHooks(app: App, regions: Loader[TextureRegion]): Unit = {
    app.addResource("world-input-keyboard", Keyboard())
  }

  private def loadFonts(app: App, regions: Loader[TextureRegion]): Unit = {
    app.addResource("font:monospace-default", Font.fromTtfFile("pt-mono/PTM55FT.ttf"))
    app.addResource("font:monospace-default-masked", app.resource[Font]("font:monospace-default").createMaskedInstance(maskChar = Font.DEFAULT_MASK_CHAR, deleteSource = false))
  }

  private def loadImages(app: App, regions: Loader[TextureRegion]): Unit = {
    app.addResource("animation:capguy-walk", Animation(regions("animations/capguy-walk.png"), nx = 8, ny = 1, dt = 0.1, mode = PlayMode.LOOP))
    app.addResource("animation:capguy-walk:instance-0", app.resource[Animation]("animation:capguy-walk").newInstance(t0 = app.localAppTime))
    app.addResource("image:test-image", StaticImage(regions("images/test-image.png")))
  }

  private def loadParticleEffects(app: App, regions: Loader[TextureRegion]): Unit = {
    app.addResource("particle-effect:test-effect:instance-0", Particles.standard("particle-effects/test-effect.party", "particle-effects/").scaleEffect(0.5f))
    def effect0 = app.resource[ParticleSource]("particle-effect:test-effect:instance-0")
    app.addResource("particle-effect:test-effect:instance-1", effect0.copy.scaleEffect(0.5f).flipY().setTint(Color.TEAL))
    app.addResource("particle-effect:test-effect:instance-2", effect0.copy.scaleEffect(0.25f))
    app.addResource("camera-position", Vec2(x = 320.0f, y = 240.0f))
    app.addResource("cool-cursor", createCursor("cursors/c2.png"))
  }

  private def loadBackground(app: App, regions: Loader[TextureRegion]): Unit = {
    app.addResource("bg-image", StaticImage(regions("backgrounds/bgtest2.jpg")))
    app.addResource("background-0",
      MultiLayer[Image]() {
        _.layer(translationScale = 0.5f, camZero = Vec2(320.0f, 240.0f)) {
          _.piece(
            bounds = Box2(ll = Vec2(0.0f, 0.0f), size = Vec2(640.0f, 480.0f)),
            image = app.resource[Image]("bg-image")
          )
        }.layer(translationScale = 0.75f, camZero = Vec2(320.0f, 240.0f)) {
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

  private def loadGui(app: App, regions: Loader.InMemory[TextureRegion]): Unit = {
    app.addResource("gui:main-menu", RootGui(debug = true))

    val menu = app.resource[RootGui]("gui:main-menu")
    val stage = menu.stage
    val table = menu.table

    table
      .addStyle("fill", classOf[TextureRegion])(regions("filled-texture"))
      .addStyle(classOf[BitmapFont])(app.resource[Font]("font:monospace-default"))
      .addStyle("masked-font", classOf[BitmapFont])(app.resource[Font]("font:monospace-default-masked"))
      .addStyle(classOf[TextButtonStyle])(new TextButtonStyle {
        val standard = table.newDrawable(style = "fill", Color.DARK_GRAY)
        val highlighted = table.newDrawable(style = "fill", Color.LIGHT_GRAY)
        up = standard
        down = standard
        over = highlighted
        font = table.style[BitmapFont]
      })
      .addStyle("default:keyboard-focus", classOf[TextButtonStyle])(new TextButtonStyle(table.style[TextButtonStyle]) {
        val kbFocus = table.newDrawable(style = "fill", Color.LIME)
        up = kbFocus
        down = kbFocus
      })
      .addStyle(classOf[LabelStyle])(new LabelStyle() {
        font = table.style[BitmapFont]
        fontColor = Color.CHARTREUSE
      })

    val fontScale = 1.5f * 1.35f
    val startBtn = new TextButton("start", table.skin).fontScale(fontScale)
    val optionsBtn = new TextButton("options", table.skin).fontScale(fontScale)
    val exitBtn = new TextButton("exit", table.skin).fontScale(fontScale)
    val menuButtons = Seq(startBtn, optionsBtn, exitBtn)

    startBtn.onClick(menu.hide())
    exitBtn.onClick(app.close())
    optionsBtn.onClick(app.addResource("controls-inverted", !app.getResource[Boolean]("controls-inverted").getOrElse(false)))

    for ((btn, i) <- menuButtons.zipWithIndex) {
      def go(di: Int) = menuButtons((menuButtons.size + i + di) % menuButtons.size).setKeyFocus()
      btn.on {
        case KeyDown(Keys.DOWN) => go(+1)
        case KeyDown(Keys.UP)   => go(-1)
      }
      btn.onKeyFocusGained(_.setStyle(menu.style[TextButtonStyle]("default:keyboard-focus")))
      btn.onKeyFocusLost(_.setStyle(menu.style[TextButtonStyle]))
      btn.mapKeyDownToClick(Keys.ENTER)
      btn.onClick(btn.setKeyFocus())
    }

    val menuItemPad = 40
    val btnWidth = 200.0f
    table
      .rw(_.cell().height(120))
      .rw(_.cell(new Label("THE COOLEST GAME", table.skin).fontScale(3.5f)).center())
      .rw(_.cell().height(menuItemPad * 2))
      .rw(_.cell(startBtn).width(btnWidth).center())
      .rw(_.cell().height(menuItemPad))
      .rw(_.cell(optionsBtn).width(btnWidth).center())
      .rw(_.cell().height(menuItemPad))
      .rw(_.cell(exitBtn).width(btnWidth).center())
      .rw(_.cell().height(menuItemPad))
      .rw(_.cell().expandY())
      .rw(_.cell(new Label("Copyright (c) Idiot ltd", table.skin).fontScale(0.8f)).center())
      .rw(_.cell().height(menuItemPad * 0.5f))

    startBtn.setKeyFocus()
    stage.blockInputEventPropagation()
  }
}

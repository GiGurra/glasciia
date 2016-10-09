package se.gigurra.glasciia.test1.testcomponents

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.{BitmapFont, TextureRegion}
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import se.gigurra.glasciia.{App, Font, Loader}
import se.gigurra.glasciia.Glasciia._

/**
  * Created by johan on 2016-10-09.
  */
object addDefaultGuiStyles {

  def apply(app: App, skin: Skin, regions: Loader.InMemory[TextureRegion]): Unit = {
    skin
      .addStyle("fill", classOf[TextureRegion])(regions("filled-texture"))
      .addStyle(classOf[BitmapFont])(app.resource[Font]("font:monospace-default"))
      .addStyle("masked-font", classOf[BitmapFont])(app.resource[Font]("font:monospace-default-masked"))
      .addStyle(classOf[TextButtonStyle])(new TextButtonStyle {
        val standard = skin.newInstance("fill", Color.DARK_GRAY)
        val highlighted = skin.newInstance("fill", Color.LIGHT_GRAY)
        up = standard
        down = standard
        over = highlighted
        font = skin.style[BitmapFont]
      })
      .addStyle("default:keyboard-focus", classOf[TextButtonStyle])(new TextButtonStyle(skin.style[TextButtonStyle]) {
        val kbFocus = skin.newInstance("fill", Color.LIME)
        up = kbFocus
        down = kbFocus
      })
      .addStyle(classOf[LabelStyle])(new LabelStyle() {
        font = skin.style[BitmapFont]
        fontColor = Color.CHARTREUSE
      })
  }
}

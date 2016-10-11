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
      .addStyle[TextureRegion]("fill", regions("filled-texture"))
      .addStyle[BitmapFont](app.resource[BitmapFont]("font:monospace-default"))
      .addStyle[BitmapFont]("masked-font", app.resource[BitmapFont]("font:monospace-default-masked"))
      .addStyle[TextButtonStyle](new TextButtonStyle {
        val standard = skin.newInstance("fill", Color.DARK_GRAY)
        val highlighted = skin.newInstance("fill", Color.LIGHT_GRAY)
        up = standard
        down = standard
        over = highlighted
        font = skin.style[BitmapFont]
      })
      .addStyle[TextButtonStyle]("default:keyboard-focus", new TextButtonStyle(skin.style[TextButtonStyle]) {
        val kbFocus = skin.newInstance("fill", Color.LIME)
        up = kbFocus
        down = kbFocus
      })
      .addStyle[LabelStyle](new LabelStyle {
        font = skin.style[BitmapFont]
        fontColor = Color.CHARTREUSE
      })
  }
}
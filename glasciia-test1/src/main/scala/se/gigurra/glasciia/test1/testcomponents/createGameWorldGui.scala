package se.gigurra.glasciia.test1.testcomponents

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.{BitmapFont, TextureRegion}
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.scenes.scene2d.ui.{Label, TextButton}
import se.gigurra.glasciia.AppEvent.KeyDown
import se.gigurra.glasciia.Glasciia._
import se.gigurra.glasciia._

/**
  * Created by johan on 2016-10-09.
  */
object createGameWorldGui {

  def apply(app: App, regions: Loader.InMemory[TextureRegion]): Stage = {
    val gui: Gui = RootGui(debug = true)
    val stage = gui.stage
    val table = gui.table

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

    stage
  }
}

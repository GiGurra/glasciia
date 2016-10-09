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
    val menu: Gui = RootGui(debug = true)
    val stage = menu.stage

    stage
  }
}

package se.gigurra.glasciia.test1.testcomponents

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.g2d.{Batch, TextureRegion}
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.{Label, TextButton}
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import se.gigurra.glasciia.AppEvent.KeyDown
import se.gigurra.glasciia._
import se.gigurra.glasciia.Glasciia._

/**
  * Created by johan on 2016-10-09.
  */
object createMainMenu {

  def apply(app: App, batch: Batch, regions: Loader.InMemory[TextureRegion]): Stage = {
    val (stage, menu) = RootGui(batch)
    val skin = menu.debug(true).skin

    addDefaultGuiStyles(app, skin, regions)

    val fontScale = 2.0f
    val startBtn = new TextButton("start", skin).fontScale(fontScale)
    val optionsBtn = new TextButton("options", skin).fontScale(fontScale)
    val exitBtn = new TextButton("exit", skin).fontScale(fontScale)
    val menuButtons = Seq(startBtn, optionsBtn, exitBtn)

    startBtn.onClick(stage.hide())
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
      btn.onClick(_.setKeyFocus())
    }

    val menuItemPad = 40
    val btnWidth = 200.0f
    menu
      .rw(_.cell().height(120))
      .rw(_.cell(new Label("THE COOLEST GAME", skin).fontScale(3.5f)).center())
      .rw(_.cell().height(menuItemPad * 2))
      .rw(_.cell(startBtn).width(btnWidth).center())
      .rw(_.cell().height(menuItemPad))
      .rw(_.cell(optionsBtn).width(btnWidth).center())
      .rw(_.cell().height(menuItemPad))
      .rw(_.cell(exitBtn).width(btnWidth).center())
      .rw(_.cell().height(menuItemPad))
      .rw(_.cell().expandY())
      .rw(_.cell(new Label("Copyright (c) Idiot ltd", skin).fontScale(0.8f)).center())
      .rw(_.cell().height(menuItemPad * 0.5f))

    startBtn.setKeyFocus()
    stage.blockInputEventPropagation()

    stage
  }
}

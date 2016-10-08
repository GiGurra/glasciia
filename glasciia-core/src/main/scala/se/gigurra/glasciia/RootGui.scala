package se.gigurra.glasciia

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.{Skin, Table}

/**
  * Created by johan on 2016-10-08.
  */
class RootGui(fillParent: Boolean,
              val stage: Stage,
              skin: Skin,
              var hidden: Boolean,
              debug: Boolean) extends Gui[Table](new Table(skin), fillParent = fillParent, debug = debug) {

  stage.addActor(table)

  def hide(): Unit = hidden = true
  def show(): Unit = hidden = false
}

object RootGui {
  def apply(fillParent: Boolean = true,
            stage: Stage = new Stage(),
            skin: Skin = new Skin(),
            hidden: Boolean = false,
            debug: Boolean = false): RootGui = {
    new RootGui(
      fillParent = fillParent,
      stage = stage,
      skin = skin,
      hidden = hidden,
      debug = debug
    )
  }
}

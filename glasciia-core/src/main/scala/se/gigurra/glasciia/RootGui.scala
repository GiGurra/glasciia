package se.gigurra.glasciia

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.{Skin, Table}

/**
  * Created by johan on 2016-10-08.
  */
object RootGui {

  def apply(fillParent: Boolean = true,
            stage: Stage = new Stage(),
            skin: Skin = new Skin(),
            debug: Boolean = false): Gui = {

    val out = Gui(table = new Table(skin), fillParent = fillParent)
    stage.addActor(out.table)
    out.table.setDebug(debug)
    out
  }
}

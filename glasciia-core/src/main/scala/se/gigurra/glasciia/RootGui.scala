package se.gigurra.glasciia

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.{Skin, Table}

/**
  * Created by johan on 2016-10-08.
  */
object RootGui {

  def apply(stage: Stage = new Stage(),
            skin: Skin = new Skin(),
            debug: Boolean = false,
            fillParent: Boolean = true): Table = {

    val table = new Table(skin)
    stage.addActor(table)
    table.setFillParent(fillParent)
    table.setDebug(debug)
    table
  }
}

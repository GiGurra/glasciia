package se.gigurra.glasciia

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.{Skin, Table}

/**
  * Created by johan on 2016-10-08.
  */
object RootGui {

  def apply(skin: Skin = new Skin): (Stage, Table) = {
    val stage = new Stage()
    val table = new Table(skin)
    table.setFillParent(true)
    stage.addActor(table)
    (stage, table)
  }
}

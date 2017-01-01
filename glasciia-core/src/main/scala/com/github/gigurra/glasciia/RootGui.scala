package com.github.gigurra.glasciia

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.{Skin, Table}
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.viewport.ScalingViewport

/**
  * Created by johan on 2016-10-08.
  */
class RootGui(protected val skin: Skin = new Skin) extends Gui {
  val stage: Stage = new Stage(new ScalingViewport(Scaling.stretch, Gdx.graphics.getWidth, Gdx.graphics.getHeight, new OrthographicCamera))
  protected val rootTable: Table = new Table(skin)
  rootTable.setFillParent(true)
  stage.addActor(rootTable)
}

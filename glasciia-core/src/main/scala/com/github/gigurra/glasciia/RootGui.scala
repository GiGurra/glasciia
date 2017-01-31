package com.github.gigurra.glasciia

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatcher
import com.badlogic.gdx.scenes.scene2d.{Actor, Stage}
import com.badlogic.gdx.scenes.scene2d.ui.{Skin, Table}
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.viewport.ScalingViewport
import com.github.gigurra.glasciia.GameEvent.InputEvent
import com.github.gigurra.glasciia.Glasciia._

/**
  * Created by johan on 2016-10-08.
  */
class RootGui(protected val skin: Skin = new Skin,
              protected val batch: SpriteBatcher = new SpriteBatcher) extends Gui {
  val stage: Stage = new Stage(new ScalingViewport(Scaling.stretch, Gdx.graphics.getWidth, Gdx.graphics.getHeight, new OrthographicCamera), batch)
  protected val rootTable: Table = new Table(skin)
  rootTable.setFillParent(true)
  stage.addActor(rootTable)

  protected implicit def _actionRunner: Actor = stage.getRoot

  override def draw(canvas: Canvas,
                    dt: Float,
                    screenFitting: Scale,
                    transform: Transform): Unit = {
    canvas.drawGui(
      stage = stage,
      dt = dt,
      screenFitting = screenFitting,
      transform = transform
    )
  }

  override def inputHandler: PartialFunction[InputEvent, Unit] = {
    stage
  }
}

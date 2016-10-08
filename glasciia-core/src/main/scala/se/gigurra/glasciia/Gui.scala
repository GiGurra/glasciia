package se.gigurra.glasciia

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.{Skin, Table}

import scala.language.implicitConversions

/**
  * Created by johan on 2016-10-08.
  */
case class Gui(skin: Skin = new Skin(),
               stage: Stage = new Stage(),
               var hidden: Boolean = false) {

  val table = new Table(skin)
  table.setFillParent(true)
  stage.addActor(table)

  def draw(canvas: Canvas, dt: Float = 0.0f): Unit = {
    if (!hidden) {
      stage.getViewport.update(canvas.width, canvas.height, true)
      if (dt != 0.0f)
        stage.act(dt)
      stage.draw()
    }
  }
  def hide(): Unit = hidden = true
  def show(): Unit = hidden = false
}

object Gui {
  implicit def gui2stage(gui: Gui): Stage = gui.stage
}
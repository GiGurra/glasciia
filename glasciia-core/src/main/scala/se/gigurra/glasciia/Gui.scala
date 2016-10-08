package se.gigurra.glasciia

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.{Skin, Table}
import com.badlogic.gdx.utils.viewport.FillViewport

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

  stage.setViewport(new FillViewport(640, 480))

  def draw(canvas: Canvas, dt: Float = 0.0f): Unit = {
    if (!hidden) {
      stage.getViewport.setScreenSize(canvas.width, canvas.height)
      stage.getViewport.setWorldSize(canvas.width*canvas.aspectRatio, canvas.height)
      stage.getViewport.update(canvas.width, canvas.height, true)
      table.layout()
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

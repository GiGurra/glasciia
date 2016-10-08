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

  def draw(canvas: Canvas, dt: Float = 0.0f): RootGui = {
    if (!hidden) {
      stage.getViewport.setScreenSize(canvas.width, canvas.height)
      stage.getViewport.setWorldSize(canvas.width, canvas.height)
      stage.getViewport.update(canvas.width, canvas.height, true)
      table.layout()
      if (dt != 0.0f)
        stage.act(dt)
      stage.draw()
    }
    this
  }

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

package se.gigurra.glasciia.test1.testcomponents

import com.badlogic.gdx.Gdx
import se.gigurra.glasciia._

/**
  * Created by johan on 2016-10-08.
  */
object drawGui {
  def apply(canvas: Canvas, gui: RootGui, dt: Float = Gdx.graphics.getDeltaTime): Unit = {
    gui.draw(canvas, dt)
  }
}

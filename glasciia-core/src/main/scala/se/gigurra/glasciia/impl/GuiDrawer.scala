package se.gigurra.glasciia.impl

import com.badlogic.gdx.Gdx
import se.gigurra.glasciia.Gui.Scaling
import se.gigurra.glasciia.RootGui
import se.gigurra.math.Box2

/**
  * Created by johan on 2016-10-08.
  */
trait GuiDrawer {

  def screenBounds: Box2[Int]
  def aspectRatio: Float

  def drawGui(gui: RootGui,
              dt: Float = Gdx.graphics.getDeltaTime,
              drawBounds: Box2[Int] = screenBounds,
              scaling: Scaling = Scaling.ONE): Unit = {
    val stage = gui.stage
    if (!gui.hidden) {
      val scale = scaling.apply(drawBounds.size)
      stage.getViewport.setScreenBounds(drawBounds.ll.x, drawBounds.ll.y, drawBounds.width, drawBounds.height)
      stage.getViewport.setWorldSize(drawBounds.width / scale, drawBounds.height / scale)
      stage.getViewport.update(drawBounds.width, drawBounds.height, true)
      if (dt != 0.0f)
        stage.act(dt)
      stage.draw()
    }
  }
}

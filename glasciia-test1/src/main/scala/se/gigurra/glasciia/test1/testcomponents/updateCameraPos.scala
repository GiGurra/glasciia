package se.gigurra.glasciia.test1.testcomponents

import com.badlogic.gdx.Gdx
import se.gigurra.glasciia.{Canvas, Keyboard, Pov4W}
import se.gigurra.math.Vec2

/**
  * Created by johan on 2016-10-08.
  */
object updateCameraPos {
  def apply(canvas: Canvas): Unit = {
    val worldInputKeyboard = canvas.app.resource[Keyboard]("world-input-keyboard")
    val app = canvas.app
    val speed = 100.0f
    val dr = Pov4W(worldInputKeyboard.isKeyDown).dir.toFloat * Gdx.graphics.getDeltaTime * speed
    val prevCameraPos = app.resource[Vec2[Float]]("camera-position")
    app.addResource("camera-position", prevCameraPos + dr)
  }
}

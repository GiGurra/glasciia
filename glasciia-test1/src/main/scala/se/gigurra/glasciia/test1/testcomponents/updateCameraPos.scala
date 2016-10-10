package se.gigurra.glasciia.test1.testcomponents

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import se.gigurra.glasciia.{Canvas, Keyboard, Pov4W}
import se.gigurra.glasciia.Glasciia._
import se.gigurra.math.Vec2

/**
  * Created by johan on 2016-10-08.
  */
object updateCameraPos {
  def apply(canvas: Canvas): Unit = {
    val keyboard = canvas.app.resource[Keyboard]("world-input-keyboard")
    val speed = if (keyboard.isKeyDown(Keys.SHIFT_LEFT) || keyboard.isKeyDown(Keys.SHIFT_RIGHT)) 200.0f else 100.0f
    val dr = Pov4W(keyboard).dir.toFloat * Gdx.graphics.getDeltaTime * speed
    canvas.camera.position.set((canvas.camera.position : Vec2[Float]) + dr)
  }
}

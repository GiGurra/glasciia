package se.gigurra.glasciia.test1.testcomponents

import com.badlogic.gdx.scenes.scene2d.Stage
import se.gigurra.glasciia.Scale.{Constant, LinearShortestSide}
import se.gigurra.glasciia._
import se.gigurra.math.Vec2

/**
  * Created by johan on 2016-10-08.
  */
object drawMenu {
  def apply(canvas: Canvas): Unit = {
    val stage = canvas.app.resource[Stage]("gui:main-menu")
    canvas.drawGui(
      stage = stage,
      scaling = LinearShortestSide(reference = Vec2(640, 480)) * Constant(0.75f)
    )
  }
}

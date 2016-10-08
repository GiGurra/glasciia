package se.gigurra.glasciia.test1.testcomponents

import se.gigurra.glasciia.Gui.Scaling.{Constant, LinearShortestSide}
import se.gigurra.glasciia._
import se.gigurra.math.Vec2

/**
  * Created by johan on 2016-10-08.
  */
object drawMenu {
  def apply(canvas: Canvas): Unit = {
    canvas.drawGui(
      gui = canvas.app.resource[RootGui]("gui:main-menu"),
      scaling = LinearShortestSide(reference = Vec2(640, 480)) * Constant(0.75f)
    )
  }
}

package se.gigurra.glasciia.test1.testcomponents

import se.gigurra.glasciia._

/**
  * Created by johan on 2016-10-08.
  */
object drawMenu {
  def apply(canvas: Canvas): Unit = {
    canvas.drawGui(canvas.app.resource[RootGui]("gui:main-menu"))
  }
}

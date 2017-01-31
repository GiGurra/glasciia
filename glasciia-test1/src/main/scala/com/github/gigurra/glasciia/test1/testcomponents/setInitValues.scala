package com.github.gigurra.glasciia.test1.testcomponents

import com.github.gigurra.glasciia._

/**
  * Created by johan on 2016-10-08.
  */
object setInitValues {
  def apply(app: Game, canvas: Canvas): Unit = {
    canvas.setCameraPos(canvas.screenSize / 2.0f)
  }
}

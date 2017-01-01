package com.github.gigurra.glasciia.test1.testcomponents

import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.gigurra.glasciia.Scale.{Constant, LinearShortestSide}
import com.github.gigurra.glasciia._
import com.github.gigurra.math.Vec2

/**
  * Created by johan on 2016-10-08.
  */
object drawGameGui {
  def apply(canvas: Canvas, resources: ResourceManager): Unit = {
    val stage = resources[Stage]("gui:game-world")
    canvas.drawGui(
      stage = stage,
      screenFitting = LinearShortestSide(reference = Vec2(640, 480)) * Constant(0.75f)
    )
  }
}

package com.github.gigurra.glasciia.test1.testcomponents

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.gigurra.glasciia.{Canvas, Pov4W}
import com.github.gigurra.glasciia.Glasciia._
import com.github.gigurra.math.Vec2

/**
  * Created by johan on 2016-10-08.
  */
object updateWorld {
  def apply(canvas: Canvas): Unit = {
    val mainMenu = canvas.app.resource[Stage]("gui:main-menu")
    if (mainMenu.hidden) {
      val speed = if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT)) 200.0f else 100.0f
      val dr = Pov4W().dir.toFloat * Gdx.graphics.getDeltaTime * speed
      canvas.camera.position.set((canvas.camera.position : Vec2[Float]) + dr)
    }
  }
}

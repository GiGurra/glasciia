package com.github.gigurra.glasciia.impl

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.gigurra.glasciia.Scale
import com.github.gigurra.math.Box2

/**
  * Created by johan on 2016-10-08.
  */
trait GuiDrawer {

  def screenBounds: Box2
  def aspectRatio: Float

  def drawGui(stage: Stage,
              dt: Float = Gdx.graphics.getDeltaTime,
              drawBounds: Box2 = screenBounds,
              scaling: Scale = Scale.ONE): Unit = {
    val scale = scaling.apply(drawBounds.size)
    stage.getViewport.setScreenBounds(drawBounds.ll.x.toInt, drawBounds.ll.y.toInt, drawBounds.width.toInt, drawBounds.height.toInt)
    stage.getViewport.setWorldSize(drawBounds.width / scale, drawBounds.height / scale)
    stage.getViewport.update(drawBounds.width.toInt, drawBounds.height.toInt, true)
    if (dt != 0.0f)
      stage.act(dt)
    stage.draw()
  }
}

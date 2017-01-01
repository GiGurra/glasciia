package com.github.gigurra.glasciia.impl

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.gigurra.glasciia.{Scale, Transform}
import com.github.gigurra.glasciia.Glasciia._
import com.github.gigurra.math.{Box2, Vec2}

/**
  * Created by johan on 2016-10-08.
  */
trait GuiDrawer {

  def screenBounds: Box2
  def aspectRatio: Float

  def drawGui(stage: Stage,
              dt: Float = Gdx.graphics.getDeltaTime,
              screenFitting: Scale = Scale.ONE,
              transform: Transform = Transform.IDENTITY): Unit = {

    stage.getRoot.setTransform(transform)

    val bounds: Box2 = screenBounds
    val screenWorldSize = Vec2(bounds.width, bounds.height) / screenFitting(bounds.size)
    stage.getViewport.setWorldSize(screenWorldSize.x, screenWorldSize.y)
    stage.getViewport.update(bounds.width.toInt, bounds.height.toInt, true)

    if (dt != 0.0f)
      stage.act(dt)

    stage.draw()
  }

}

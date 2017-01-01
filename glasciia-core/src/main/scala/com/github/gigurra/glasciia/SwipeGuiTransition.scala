package com.github.gigurra.glasciia

import com.github.gigurra.math.Vec2

/**
  * Created by johan on 2017-01-01.
  */
class SwipeGuiTransition(direction: Vec2 = Vec2(1.0f, 0.0f),
                         interpolator: Float => Float = x => x) extends GuiSystem.Transition {

  override def draw(canvas: Canvas,
                    dt: Float,
                    screenFitting: Scale,
                    transform: Transform,
                    elapsed: Long,
                    transitionTime: Long,
                    from: Gui,
                    to: Gui): Unit = {

    val bounds = canvas.screenBounds
    val screenWorldSize = Vec2(bounds.width, bounds.height) / screenFitting(bounds.size)

    val factor: Float = interpolator.apply(elapsed.toFloat / transitionTime.toFloat)

    from.draw(
      canvas = canvas,
      dt = dt,
      screenFitting = screenFitting,
      transform = transform.preTranslate(factor * direction *|* screenWorldSize)
    )

    to.draw(
      canvas = canvas,
      dt = dt,
      screenFitting = screenFitting,
      transform = transform.preTranslate(factor * direction *|* screenWorldSize).preTranslate(-direction *|* screenWorldSize)
    )
  }
}

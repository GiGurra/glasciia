package com.github.gigurra.glasciia

import com.github.gigurra.math.Vec2

/**
  * Created by johan on 2017-01-01.
  */
class RotateGuiTransition(ccw: Boolean = true,
                          angleRange: Float = 90.0f,
                          relativeLocation: Vec2 = Vec2(0.0f, 0.0f),
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

    val joint = screenWorldSize *|* relativeLocation

    val direction: Float = if (ccw) 1.0f else -1.0f

    val transformOut =
      Transform
        .translate(joint)
        .rotate(direction * angleRange * factor)
        .translate(-joint)
        .mul(transform)

    val startInfrom: Float = if (ccw) -angleRange else angleRange

    val transformIn =
      Transform
        .translate(joint)
        .rotate(startInfrom + direction * angleRange * factor)
        .translate(-joint)
        .mul(transform)

    from.draw(
      canvas = canvas,
      dt = dt,
      screenFitting = screenFitting,
      transform = transformOut
    )

    to.draw(
      canvas = canvas,
      dt = dt,
      screenFitting = screenFitting,
      transform = transformIn
    )
  }
}

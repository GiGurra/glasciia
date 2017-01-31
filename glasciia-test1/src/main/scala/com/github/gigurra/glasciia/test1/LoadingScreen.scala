package com.github.gigurra.glasciia.test1

import com.badlogic.gdx.graphics.Color
import com.github.gigurra.glasciia.GameEvent._
import com.github.gigurra.glasciia._
import com.github.gigurra.math.Vec2

/**
  * Created by johan on 2016-10-31.
  */
class LoadingScreen(canvas: Canvas) extends Game(canvas) with Logging {

  private val loadingScreenTextures = TextureRegionLoader.newDefault()()
  private val someImg = loadingScreenTextures("images/test-image.png")
  loadingScreenTextures.flush()

  def eventHandler: PartialFunction[GameEvent, Unit] = {

    case Render(time, _) =>
      canvas.drawFrame(
        pixelViewport = canvas.screenBounds,
        clearBuffer = Some(loadingSreenColor(time)),
        camPos = Vec2(0.0f, 0.0f)
      ) {

      }
    case Exit(_, _) =>
      log.info("LoadingScreen disposing resources")
      loadingScreenTextures.dispose()
  }

  private def loadingSreenColor(time: Long) : Color = {
    (time / 1000L) % 3L match {
      case 0 => Color.RED
      case 2 => Color.BLUE
      case 1 => Color.GREEN
    }
  }
}

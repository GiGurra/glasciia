package se.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.g2d.TextureRegion
import se.gigurra.math.{One, Vec2, Zero}
import se.gigurra.glasciia.Glasciia._

/**
  * Created by johan on 2016-10-01.
  */
trait ImageDrawer { self: ContentDrawer =>

  def drawImage(image: TextureRegion,
                at: Vec2[Float] = Zero.vec2f,
                scale: Vec2[Float] = One.vec2f,
                rotate: Float = 0.0f,
                normalizeScale: Boolean = true): Unit = {

    val frameSize = image.size
    val normalizedScale =
      if (normalizeScale)
        Vec2(scale.x / frameSize.x, scale.y / frameSize.y)
      else
        scale

    draw(at, normalizedScale, rotate) {
      batch.draw(image, 0.0f, 0.0f)
    }
  }
}

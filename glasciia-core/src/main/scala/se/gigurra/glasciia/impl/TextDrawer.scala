package se.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.Color
import se.gigurra.glasciia.Font
import se.gigurra.math.{Vec2, Zero}

/**
  * Created by johan on 2016-10-01.
  */
trait TextDrawer { self: ContentDrawer =>

  def drawString(char: String,
                 font: Font,
                 color: Color,
                 at: Vec2[Float] = Zero[Vec2[Float]],
                 scale: Float = 1.0f,
                 rotate: Float = 0.0f,
                 normalizeFontScale: Boolean = true): Unit = {
    draw(at, if (normalizeFontScale) Vec2(scale / font.size, scale / font.size) else Vec2(scale, scale), rotate) {
      font.setColor(color)
      font.draw(batch, char, 0.0f, 0.0f)
    }
  }
}

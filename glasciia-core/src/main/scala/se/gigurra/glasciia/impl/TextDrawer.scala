package se.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Align
import se.gigurra.glasciia.Font
import se.gigurra.glasciia.impl.TextDrawer.Anchor
import se.gigurra.math.{Vec2, Zero}

/**
  * Created by johan on 2016-10-01.
  */
trait TextDrawer { self: ContentDrawer =>

  def drawText(text: String,
               font: Font,
               color: Color,
               at: Vec2[Float] = Zero.vec2f,
               scale: Float = 1.0f,
               rotate: Float = 0.0f,
               normalizeFontScale: Boolean = true,
               anchor: Anchor = Anchor.UL,
               wrap: Float = 0.0f): Unit = {

    val normalizedScale = if (normalizeFontScale) Vec2(scale / font.size, scale / font.size) else Vec2(scale, scale)

    draw(at, normalizedScale, rotate) {
      font.setColor(color)
      font.draw(batch, text, 0.0f, anchor.dy*font.lineHeight(false), wrap, anchor.halign, wrap != 0.0f)
    }
  }
}

object TextDrawer {
  case class Anchor(halign: Int, dy: Float)
  object Anchor {
    object LL extends Anchor(Align.left, FULL)
    object LC extends Anchor(Align.center, FULL)
    object LR extends Anchor(Align.right, FULL)
    object CL extends Anchor(Align.left, HALF)
    object CC extends Anchor(Align.center, HALF)
    object CR extends Anchor(Align.right, HALF)
    object UL extends Anchor(Align.left, ZERO)
    object UC extends Anchor(Align.center, ZERO)
    object UR extends Anchor(Align.right, ZERO)

    private val ZERO = 0.0f
    private val HALF = 0.5f
    private val FULL = 1.0f
  }
}


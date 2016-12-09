package com.github.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.utils.Align
import com.github.gigurra.glasciia.Glasciia._
import com.github.gigurra.glasciia.Transform
import com.github.gigurra.glasciia.impl.TextDrawer.Anchor

/**
  * Created by johan on 2016-10-01.
  */
trait TextDrawer { self: ContentDrawer =>

  def drawText(text: String,
               font: BitmapFont,
               color: Color,
               transform: Transform,
               normalizeFontScale: Boolean = true,
               anchor: Anchor = Anchor.UL,
               wrap: Float = 0.0f): Unit = {

    val normalizedTransform =
      if (normalizeFontScale)
        transform.scale(1.0f / font.size, 1.0f / font.size)
      else
        transform

    draw(normalizedTransform) {
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


package com.github.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.{BitmapFont, BitmapFontCache, GlyphLayout}
import com.badlogic.gdx.utils.Align
import com.github.gigurra.glasciia.Font
import com.github.gigurra.glasciia.Glasciia._
import com.github.gigurra.math.Vec2

import scala.language.implicitConversions

/**
  * Created by johan on 2016-10-03.
  */
trait FontImplicits {
  implicit def toFontImplicits(font: BitmapFont): FontImplicitsImpl.FontImplicitOps = {
    new FontImplicitsImpl.FontImplicitOps(font)
  }
}

object FontImplicits extends FontImplicits

object FontImplicitsImpl {

  implicit class FontImplicitOps(val bitmapFont: BitmapFont) extends AnyVal {

    def size: Float = bitmapFont.getCapHeight

    def preload(str: CharSequence,
                at: Vec2 = Vec2.zero,
                align: Int = Align.left,
                targetWidth: Float = 0.0f,
                wrap: Boolean = false,
                color: Color = null,
                alphaScale: Float = 1.0f): BitmapFontCache = {
      val cached = bitmapFont.newFontCache()
      cached.addText(new GlyphLayout(
        bitmapFont,
        str,
        Option(color).getOrElse(bitmapFont.getColor).scaleAlpha(alphaScale),
        targetWidth,
        align,
        wrap), at.x, at.y)
      cached
    }

    def spaceWidth(normalized: Boolean = true): Float = {
      if (normalized) bitmapFont.getSpaceWidth / size
      else bitmapFont.getSpaceWidth
    }

    def lineHeight(normalized: Boolean = true): Float = {
      if (normalized) bitmapFont.getLineHeight / size
      else bitmapFont.getLineHeight
    }

    def close(): Unit = {
      bitmapFont.dispose()
    }

    def createMaskedInstance(maskChar: Char, deleteSource: Boolean): BitmapFont = {
      Font.createMaskedFont(bitmapFont, maskChar, ownsTexture = deleteSource)
    }
  }

}
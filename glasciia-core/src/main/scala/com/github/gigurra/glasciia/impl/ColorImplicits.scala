package com.github.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.Color
import scala.language.implicitConversions

/**
  * Created by johan on 2016-09-28.
  */
trait ColorImplicits {
  implicit def toRichColor(color: Color): ColorImplicitsImpl.RichGdxColor = {
    new ColorImplicitsImpl.RichGdxColor(color)
  }
}

object ColorImplicits extends ColorImplicits

object ColorImplicitsImpl {

  implicit class RichGdxColor(val color: Color) extends AnyVal {
    def scaleAlpha(s: Float): Color = new Color(color.r, color.g, color.b, color.a * s)
    def scaleAlpha(s: Double): Color = new Color(color.r, color.g, color.b, color.a * s.toFloat)
    def scale(r: Float = 1.0f, g: Float = 1.0f, b: Float = 1.0f, a: Float = 1.0f): Color = new Color(color.r * r, color.g * g, color.b * b, color.a * a)
    def scaleRGB(s: Float = 1.0f): Color = scale(s,s,s)
  }
}
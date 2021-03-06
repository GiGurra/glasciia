package com.github.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.{Affine2, Vector2}
import com.github.gigurra.glasciia.{PreparedRepeatedImage, Transform}
import com.github.gigurra.glasciia.Glasciia._
import com.github.gigurra.math.Vec2

/**
  * Created by johan on 2016-10-01.
  */
trait ImageDrawer extends ZTranslationExtraction { self: ContentDrawer =>

  private val affine = new Affine2

  final def drawImage(image: TextureRegion, transform: Transform): Unit = {
    affine.set(transform)
    draw(extractZTranslation(transform))(batch.draw(image, 1.0f, 1.0f, affine))
  }

  final def drawImageRepeated(image: TextureRegion, transform: Transform, count: Int, delta: Vec2): Unit = {
    affine.set(transform)
    draw(extractZTranslation(transform))(batch.drawRepeat(image, 1.0f, 1.0f, affine, count, new Vector2(delta.x, delta.y)))
  }

  final def drawImageRepeated(lines: PreparedRepeatedImage, cornersFirst: Boolean = true): Unit = {
    affine.set(lines.transform)
    val zTranslation = extractZTranslation(lines.transform)

    def drawCorners(): Unit = {
      lines.connectionImage.foreach { connectionImage =>
        draw(zTranslation)(batch.drawRepeat(connectionImage, affine, lines.connections))
      }
    }

    def drawLines(): Unit = {
      draw(zTranslation)(batch.drawRepeat(lines.segmentImage, affine, lines.segments))
    }

    if (cornersFirst) {
      drawCorners()
    }
    drawLines()
    if (!cornersFirst) {
      drawCorners()
    }

  }
}

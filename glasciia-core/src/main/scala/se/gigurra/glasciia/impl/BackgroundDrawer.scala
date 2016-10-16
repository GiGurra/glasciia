package se.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.g2d.TextureRegion
import se.gigurra.glasciia.MultiLayer
import se.gigurra.math.{Box2, Vec2}

/**
  * Created by johan on 2016-10-01.
  */
trait BackgroundDrawer { self: ImageDrawer =>

  def cameraPos: Vec2[Float]
  def cameraBounds: Box2[Float]

  def drawBackGround(backGround: MultiLayer[TextureRegion]): Unit = {
    for {
      layer <- backGround.layers
      piece <- layer.pieces
    } {
      val translationScaleOffset = (cameraPos - layer.camZero) * (1.0f - layer.translationScale)
      val translatedPiecePos = piece.bounds.ll + translationScaleOffset

      val translatedPieceBounds = Box2(ll = translatedPiecePos, size = piece.bounds.size)
      if (cameraBounds.overlaps(translatedPieceBounds)) {
        drawImage(piece.image, at = translatedPiecePos, scale = piece.bounds.size)
      }
    }
  }
}

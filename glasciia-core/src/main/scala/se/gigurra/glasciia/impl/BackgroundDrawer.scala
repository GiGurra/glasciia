package se.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.Camera
import se.gigurra.glasciia.{Image, MultiLayer}
import se.gigurra.math.{Box2, Vec2}

/**
  * Created by johan on 2016-10-01.
  */
trait BackgroundDrawer { self: ImageDrawer =>

  def camera: Camera

  def drawBackGround(backGround: MultiLayer[Image]): Unit = {
    val cameraPos = Vec2(camera.position.x, camera.position.y)
    val cameraSize = Vec2(camera.viewportWidth, camera.viewportHeight)
    val cameraBounds = Box2(ll = cameraPos - cameraSize / 2.0f, size = cameraSize)
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

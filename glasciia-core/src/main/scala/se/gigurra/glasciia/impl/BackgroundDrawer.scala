package se.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.Camera
import se.gigurra.glasciia.BackGround
import se.gigurra.math.Vec2

/**
  * Created by johan on 2016-10-01.
  */
trait BackgroundDrawer { self: StaticImageDrawer =>

  def camera: Camera

  def drawBackGround(backGround: BackGround): Unit = {
    for {
      layer <- backGround.layers
      piece <- layer.pieces
    } {
      val offs = (Vec2(camera.position.x, camera.position.y) - layer.zero) * (1.0f - layer.translationScale)
      drawStaticImage(piece.image, at = piece.bounds.ll + offs, scale = piece.bounds.size)
    }
  }
}

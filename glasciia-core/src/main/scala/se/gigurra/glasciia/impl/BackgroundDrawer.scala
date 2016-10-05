package se.gigurra.glasciia.impl

import se.gigurra.glasciia.BackGround

/**
  * Created by johan on 2016-10-01.
  */
trait BackgroundDrawer { self: StaticImageDrawer =>

  def drawBackGround(backGround: BackGround): Unit = {
    for {
      layer <- backGround.layers
      piece <- layer.pieces
    } {
      drawStaticImage(piece.image, at = piece.bounds.ll, scale = piece.bounds.size)
    }
  }
}

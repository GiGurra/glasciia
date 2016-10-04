package se.gigurra.glasciia

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.TextureRegion
import se.gigurra.glasciia.BackGround.Layer.Piece
import se.gigurra.math.Box

/**
  * @param layers
  *               In draw order, i.e. [Furthest away ... Closest]
  */
case class BackGround(layers: Seq[BackGround.Layer]) {

}

object BackGround {
  case class Layer(bounds: Box[Float], translationScale: Float, pieces: Seq[Piece]) {
    def piecesInView(camera: Camera): Seq[Piece] = {
      pieces.filter(_.isInView(camera))
    }
  }

  object Layer {
    case class Piece(bounds: Box[Float], region: TextureRegion) {
      def isInView(camera: Camera): Boolean = {
        ???
      }
    }
  }
}

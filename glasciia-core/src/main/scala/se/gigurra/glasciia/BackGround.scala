package se.gigurra.glasciia

import com.badlogic.gdx.graphics.g2d.TextureRegion
import se.gigurra.math.Box
import scala.collection.mutable

/**
  * @param layers
  *               In draw order, i.e. [Furthest away ... Closest]
  */
case class BackGround(layers: Seq[BackGroundLayer])
case class BackGroundLayer(translationScale: Float, pieces: Seq[BackGroundPiece])
case class BackGroundPiece(bounds: Box[Float], region: TextureRegion)

object BackGround {

  def builder: _impl.Builder = new _impl.Builder()

  object _impl {

    class Builder(private[BackGround] var layers: mutable.Buffer[BackGroundLayer] = mutable.Buffer.empty) {
      def newLayer(translationScale: Float = 1.0f): LayerBuilder = new LayerBuilder(this, translationScale)
      def build(): BackGround = new BackGround(layers)
    }

    class LayerBuilder(builder: Builder, translationScale: Float, pieces: mutable.Buffer[BackGroundPiece] = mutable.Buffer.empty) {
      def addPiece(bounds: Box[Float], region: TextureRegion): LayerBuilder = {
        pieces += BackGroundPiece(bounds, region)
        this
      }
      def build(): Builder = {
        builder.layers += BackGroundLayer(translationScale, pieces)
        builder
      }
    }
  }

}

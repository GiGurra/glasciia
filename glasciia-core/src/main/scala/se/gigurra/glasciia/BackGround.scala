package se.gigurra.glasciia

import com.badlogic.gdx.graphics.g2d.TextureRegion
import se.gigurra.math.{Box, Vec2, Zero}

import scala.collection.mutable

/**
  * @param layers
  *               In draw order, i.e. [Furthest away ... Closest]
  */
case class BackGround(layers: Seq[BackGroundLayer])
case class BackGroundLayer(translationScale: Float, zero: Vec2[Float], pieces: Seq[BackGroundPiece])
case class BackGroundPiece(bounds: Box[Float], region: TextureRegion)

object BackGround {

  def builder: _impl.Builder = builder(Zero[Vec2[Float]])
  def builder(zero: Vec2[Float]): _impl.Builder = new _impl.Builder(zero)

  object _impl {

    class Builder(zero: Vec2[Float], private[BackGround] var layers: mutable.Buffer[BackGroundLayer] = mutable.Buffer.empty) {
      def newLayer(translationScale: Float = 1.0f, zero: Vec2[Float] = Builder.this.zero): LayerBuilder = new LayerBuilder(this, zero, translationScale)
      def build(): BackGround = new BackGround(layers)
    }

    class LayerBuilder(builder: Builder, zero: Vec2[Float], translationScale: Float, pieces: mutable.Buffer[BackGroundPiece] = mutable.Buffer.empty) {
      def addPiece(bounds: Box[Float], region: TextureRegion): LayerBuilder = {
        pieces += BackGroundPiece(bounds, region)
        this
      }
      def build(): Builder = {
        builder.layers += BackGroundLayer(translationScale, zero, pieces)
        builder
      }
    }
  }

}

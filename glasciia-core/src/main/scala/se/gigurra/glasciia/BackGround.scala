package se.gigurra.glasciia

import se.gigurra.math.{Box2, Vec2, Zero}

import scala.collection.mutable

/**
  * @param layers
  *               In draw order, i.e. [Furthest away ... Closest]
  */
case class BackGround(layers: Seq[BackGroundLayer])
case class BackGroundLayer(translationScale: Float, zero: Vec2[Float], pieces: Seq[BackGroundPiece])
case class BackGroundPiece(bounds: Box2[Float], image: StaticImage)

object BackGround {

  def apply(f: _impl.Builder => _impl.Builder): BackGround = apply(Zero[Vec2[Float]])(f: _impl.Builder => _impl.Builder)
  def apply(zero: Vec2[Float])(f: _impl.Builder => _impl.Builder): BackGround = new _impl.Builder(zero)(f: _impl.Builder => _impl.Builder).build()

  object _impl {

    class Builder(camZero: Vec2[Float], layers: mutable.Buffer[BackGroundLayer] = mutable.Buffer.empty) {
      def apply(f: Builder => _impl.Builder): Builder = {
        f(this)
        this
      }
      def layer(translationScale: Float = 1.0f, camZero: Vec2[Float] = Builder.this.camZero)(f: LayerBuilder => LayerBuilder): Builder = {
        val lb = new LayerBuilder(camZero, translationScale)
        f(lb)
        layers += lb.build()
        this
      }
      def build(): BackGround = new BackGround(layers)
    }

    class LayerBuilder(camZero: Vec2[Float], translationScale: Float, pieces: mutable.Buffer[BackGroundPiece] = mutable.Buffer.empty) {
      def piece(bounds: Box2[Float], image: StaticImage): LayerBuilder = {
        pieces += BackGroundPiece(bounds, image)
        this
      }
      def build(): BackGroundLayer = BackGroundLayer(translationScale, camZero, pieces)
    }
  }

}

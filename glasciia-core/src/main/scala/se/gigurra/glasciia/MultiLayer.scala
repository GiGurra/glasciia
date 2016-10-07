package se.gigurra.glasciia

import se.gigurra.math.{Box2, Vec2, Zero}

import scala.collection.mutable

/**
  * @param layers
  *               In draw order, i.e. [Furthest away ... Closest]
  */
case class MultiLayer[T](layers: Seq[Layer[T]])
case class Layer[T](translationScale: Float, camZero: Vec2[Float], pieces: Seq[Piece[T]])
case class Piece[T](bounds: Box2[Float], image: T)

object MultiLayer {

  def apply[T](zero: Vec2[Float] = Zero.vec2f)(f: _impl.Builder[T] => _impl.Builder[T]): MultiLayer[T] = new _impl.Builder[T](zero)(f: _impl.Builder[T] => _impl.Builder[T]).build()

  object _impl {

    class Builder[T](camZero: Vec2[Float], layers: mutable.Buffer[Layer[T]] = mutable.Buffer.empty[Layer[T]]) {
      def apply(f: Builder[T] => _impl.Builder[T]): Builder[T] = {
        f(this)
        this
      }
      def layer(translationScale: Float = 1.0f, camZero: Vec2[Float] = Builder.this.camZero)(f: LayerBuilder[T] => LayerBuilder[T]): Builder[T] = {
        val lb = new LayerBuilder[T](camZero, translationScale)
        f(lb)
        layers += lb.build()
        this
      }
      def build(): MultiLayer[T] = new MultiLayer(layers)
    }

    class LayerBuilder[T](camZero: Vec2[Float], translationScale: Float, pieces: mutable.Buffer[Piece[T]] = mutable.Buffer.empty[Piece[T]]) {
      def piece(bounds: Box2[Float], image: T): LayerBuilder[T] = {
        pieces += Piece[T](bounds, image)
        this
      }
      def build(): Layer[T] = Layer[T](translationScale, camZero, pieces)
    }
  }

}

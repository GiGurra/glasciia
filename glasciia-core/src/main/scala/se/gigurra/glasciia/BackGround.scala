package se.gigurra.glasciia

import se.gigurra.math.Box

/**
  * @param layers
  *               In draw order, i.e. [Furthest away ... Closest]
  */
case class BackGround(layers: Seq[BackGround.Layer]) {

}

object BackGround {
  case class Layer(bounds: Box[Float],
                   translationScale: Float) {

  }



}

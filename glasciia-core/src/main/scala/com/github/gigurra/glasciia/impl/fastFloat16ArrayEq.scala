package com.github.gigurra.glasciia.impl

/**
  * Created by johan on 2017-01-01.
  */
object fastFloat16ArrayEq {
  final def apply(ar1: Array[Float], ar2: Array[Float]): Boolean = {
    var i = 0
    while (i < 16) {
      if (ar1(i) != ar2(i)) {
        return false
      }
      i += 1
    }
    true
  }
}

package com.github.gigurra.glasciia.impl

import com.github.gigurra.glasciia.Transform

/**
  * Created by johan on 2016-12-30.
  */
trait ZOffset {

  private val tempZTransform = new Transform(Mat4Mutable())

  protected final def zTransform(transform: Transform): Transform = {
    if (transform.zTranslation == 0.0f) {
      Transform.IDENTITY
    }
    else {
      tempZTransform.data(14) = transform.zTranslation * transform.scaleZ
      tempZTransform
    }
  }
}

package com.github.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.g2d.{Batch, SpriteBatch}

/**
  * Created by johan on 2016-10-01.
  */
trait Batcher {
  val batch: Batch = new SpriteBatch()
}

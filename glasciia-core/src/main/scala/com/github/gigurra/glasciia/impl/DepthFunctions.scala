package com.github.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.g2d.SpriteBatcher

/**
  * Created by johan on 2016-12-30.
  */
trait DepthFunctions {

  def batch: SpriteBatcher

  final def withDepthTest(use: Boolean, write: Boolean, func: Int)(content: => Unit): Unit = {
    val prevUse = depthTestEnabled
    val prevWrite = depthWriteEnabled
    val prevFunc = depthFunc
    batch.setupDepthTest(use, write, func)
    content
    batch.setupDepthTest(prevUse, prevWrite, prevFunc)
  }

  final def setDepthTest(use: Boolean, write: Boolean, func: Int): Unit = {
    batch.setupDepthTest(use, write, func)
  }

  final def resetDepthTest(): Unit = {
    batch.setupDepthTest(false, true, GlConstants.GL_LEQUAL)
  }

  final def depthTestEnabled: Boolean = {
    batch.getDepthTest
  }

  final def depthWriteEnabled: Boolean = {
    batch.getDepthWrite
  }

  final def depthFunc: Int = {
    batch.getDepthFunc
  }
}

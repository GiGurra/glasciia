package com.github.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.g2d.SpriteBatcher
import com.github.gigurra.glasciia.Glasciia._

/**
  * Created by johan on 2016-12-30.
  */
trait ColorFunctions {

  def batch: SpriteBatcher

  final def setBlending(state: Boolean): Unit = {
    if (state) {
      batch.enableBlending()
    } else {
      batch.disableBlending()
    }
  }

  final def enableBlending(): Unit = {
    setBlending(true)
  }

  final def disableBlending(): Unit = {
    setBlending(false)
  }

  final def setBlendFunc(srcFactor: Int, dstFactor: Int): Unit = {
    batch.setBlendFunction(srcFactor, dstFactor)
  }

  final def setBlendSrcAlphaOneMinusSrAlpha(): Unit = {
    setBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
  }

  final def useColorMask(r: Boolean, g: Boolean, b: Boolean, a: Boolean)(content: => Unit): Unit = {
    val prevR = rMask
    val prevG = gMask
    val prevB = bMask
    val prevA = aMask
    colorMask(r, g, b, a)
    content
    colorMask(prevR, prevG, prevB, prevA)
  }

  final def useColorMaskFalse(content: => Unit): Unit = {
    useColorMask(r = false, g = false, b = false, a = false)(content)
  }

  final def colorMask(r: Boolean, g: Boolean, b: Boolean, a: Boolean): Unit = {
    batch.setupColorMasks(r, g, b, a)
  }

  final def colorMaskFalse(): Unit = {
    colorMask(r = false, g = false, b = false, a = false)
  }

  final def rMask: Boolean = {
    batch.getRedMask
  }

  final def gMask: Boolean = {
    batch.getGreenMask
  }

  final def bMask: Boolean = {
    batch.getBlueMask
  }

  final def aMask: Boolean = {
    batch.getAlphaMask
  }
}

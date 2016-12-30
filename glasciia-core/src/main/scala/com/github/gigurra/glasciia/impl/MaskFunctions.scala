package com.github.gigurra.glasciia.impl

import com.badlogic.gdx.Gdx
import com.github.gigurra.glasciia.Canvas
import com.github.gigurra.glasciia.Glasciia._

/**
  * Created by johan on 2016-12-30.
  */
trait MaskFunctions { self: Canvas =>

  final def drawInvisibleMask(content: => Unit): Unit = {
    withDepthTest(use = true, write = true, GL_ALWAYS) {
      useColorMaskFalse {
        content
      }
    }
  }

  final def drawVisibleMask(content: => Unit): Unit = {
    withDepthTest(use = true, write = true, GL_ALWAYS) {
      content
    }
  }

  final def drawInsideMask(content: => Unit): Unit = {
    withDepthTest(use = true, write = true, GL_EQUAL) {
      content
    }
  }

  final def drawOutsideMask(content: => Unit): Unit = {
    withDepthTest(use = true, write = true, GL_NOTEQUAL) {
      content
    }
  }

  final def clearMask(): Unit = {
    batch.flush()
    withDepthTest(use = true, write = true, GL_ALWAYS) {
      Gdx.gl.glClear(GL_DEPTH_BITS)
    }
  }
}

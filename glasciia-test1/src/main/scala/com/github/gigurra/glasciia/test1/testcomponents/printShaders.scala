package com.github.gigurra.glasciia.test1.testcomponents

import com.badlogic.gdx.graphics.g2d.Batch
import com.github.gigurra.glasciia.Logging

/**
  * Created by johan on 2016-10-08.
  */
object printShaders extends Logging {
  def apply(batch: Batch): Unit = {
    log.info("                    ")
    log.info("VERTEX SHADER SOURCE")
    log.info("--------------------")
    log.info(batch.getShader.getVertexShaderSource)
    log.info("                    ")
    log.info("FRAGMENT SHADER SOURCE")
    log.info("----------------------")
    log.info(batch.getShader.getFragmentShaderSource)
  }
}

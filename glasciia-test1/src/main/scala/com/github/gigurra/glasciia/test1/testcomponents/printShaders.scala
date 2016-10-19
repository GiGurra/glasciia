package com.github.gigurra.glasciia.test1.testcomponents

import com.badlogic.gdx.graphics.g2d.Batch

/**
  * Created by johan on 2016-10-08.
  */
object printShaders {
  def apply(batch: Batch): Unit = {
    println()
    println("VERTEX SHADER SOURCE")
    println("--------------------")
    println(batch.getShader.getVertexShaderSource)
    println()
    println("FRAGMENT SHADER SOURCE")
    println("----------------------")
    println(batch.getShader.getFragmentShaderSource)
  }
}

package se.gigurra.glasciia.impl

import se.gigurra.glasciia.util.Shader

/**
  * Created by johan on 2016-10-01.
  */
trait DefaultShader {
  val defaultShader = Shader.fromLocation(
    vertexShader = "shaders/default-shader.vert",
    fragmentShader = "shaders/default-shader.frag"
  )
}

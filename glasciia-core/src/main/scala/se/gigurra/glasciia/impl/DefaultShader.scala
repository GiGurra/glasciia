package se.gigurra.glasciia.impl

import java.io.FileNotFoundException

import com.badlogic.gdx.graphics.glutils.ShaderProgram
import se.gigurra.io.LoadString

/**
  * Created by johan on 2016-10-01.
  */
trait DefaultShader {
  val defaultShader: ShaderProgram = new ShaderProgram(
    LoadString.from("shaders/default-shader.vert").getOrElse(throw new FileNotFoundException(s"Could not find shaders/default-shader.vert")),
    LoadString.from("shaders/default-shader.frag").getOrElse(throw new FileNotFoundException(s"Could not find shaders/default-shader.frag"))
  )
}

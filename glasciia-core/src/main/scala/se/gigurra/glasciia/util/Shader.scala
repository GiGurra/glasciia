package se.gigurra.glasciia.util

import java.io.FileNotFoundException

import com.badlogic.gdx.graphics.glutils.ShaderProgram
import se.gigurra.io.LoadString

/**
  * Created by johan on 2016-10-01.
  */
object Shader {

  def fromLocation(vertexShader: String, fragmentShader: String): ShaderProgram = {
    fromSourceCode(
      LoadString.from(vertexShader).getOrElse(throw new FileNotFoundException(s"Could not find vertex shader source file '$vertexShader'")),
      LoadString.from(fragmentShader).getOrElse(throw new FileNotFoundException(s"Could not find fragment shader source file '$fragmentShader'"))
    )
  }

  def fromSourceCode(vertexShader: String, fragmentShader: String): ShaderProgram = {
    val out = new ShaderProgram(vertexShader, fragmentShader)
    if (!out.isCompiled)
      throw new IllegalArgumentException(s"Compilation of shader failed!: ${out.getLog}")
    out
  }
}

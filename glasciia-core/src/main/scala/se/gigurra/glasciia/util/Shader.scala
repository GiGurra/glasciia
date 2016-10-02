package se.gigurra.glasciia.util

import java.io.FileNotFoundException

import com.badlogic.gdx.graphics.glutils.ShaderProgram
import se.gigurra.glasciia.Glasciia

/**
  * Created by johan on 2016-10-01.
  */
object Shader extends Glasciia {

  def fromLocation(vertexShader: String, fragmentShader: String): ShaderProgram = {
    fromSourceCode(
      LoadFile(vertexShader).getOrElse(throw new FileNotFoundException(s"Could not find vertex shader source file '$vertexShader'")).readString(),
      LoadFile(fragmentShader).getOrElse(throw new FileNotFoundException(s"Could not find fragment shader source file '$fragmentShader'")).readString()
    )
  }

  def fromSourceCode(vertexShader: String, fragmentShader: String): ShaderProgram = {
    val out = new ShaderProgram(vertexShader, fragmentShader)
    if (!out.isCompiled)
      throw new IllegalArgumentException(s"Compilation of shader failed!: ${out.getLog}")
    out
  }
}

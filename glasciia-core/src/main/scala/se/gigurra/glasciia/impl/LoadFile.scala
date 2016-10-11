package se.gigurra.glasciia.impl

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle

import scala.util.Try

/**
  * Created by johan on 2016-10-02.
  */
object LoadFile {
  def apply(location: String): Option[FileHandle] = {
    require(Gdx.files != null, s"LibGDX not yet loaded! Need to create e.g. an LWJGLApplication before using this.")
    Try(Gdx.files.internal(location)).filter(_.exists())
      .orElse(Try(Gdx.files.external(location)).filter(_.exists()))
      .orElse(Try(Gdx.files.local(location)).filter(_.exists()))
      .orElse(Try(Gdx.files.absolute(location)).filter(_.exists()))
      .toOption
  }
}

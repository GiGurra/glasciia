package se.gigurra.glasciia.impl

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle

import scala.util.Try

/**
  * Created by johan on 2016-10-02.
  */
object LoadFile {
  def apply(location: String): Option[FileHandle] = {
    Try(Gdx.files.internal(location)).filter(_.exists())
      .orElse(Try(Gdx.files.external(location)).filter(_.exists()))
      .orElse(Try(Gdx.files.local(location)).filter(_.exists()))
      .orElse(Try(Gdx.files.absolute(location)).filter(_.exists()))
      .toOption
  }
}

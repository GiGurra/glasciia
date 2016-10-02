package se.gigurra.glasciia.util

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle

import scala.util.Try

/**
  * Created by johan on 2016-10-02.
  */
object LoadFile {
  def apply(location: String): Option[FileHandle] = {
    Try(Gdx.files.internal(location))
      .orElse(Try(Gdx.files.external(location)))
      .orElse(Try(Gdx.files.local(location)))
      .orElse(Try(Gdx.files.absolute(location)))
      .toOption
  }
}

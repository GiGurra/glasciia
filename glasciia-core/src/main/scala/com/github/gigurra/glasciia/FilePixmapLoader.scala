package com.github.gigurra.glasciia

import com.badlogic.gdx.graphics.Pixmap
import com.github.gigurra.glasciia.impl.LoadFile

/**
  * Created by johan_home on 2016-10-15.
  */
case class FilePixmapLoader() extends Loader[Pixmap] {
  override def get(fileName: String, upload: Boolean): Option[Pixmap] = {
    LoadFile(fileName) match {
      case r @ Some(fileHandle) => Some(new Pixmap(fileHandle))
      case None => None
    }
  }
  override def uploadIfDirty(): Unit = {}
}
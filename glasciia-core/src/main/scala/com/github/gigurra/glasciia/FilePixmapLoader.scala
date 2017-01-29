package com.github.gigurra.glasciia

import com.badlogic.gdx.graphics.Pixmap
import com.github.gigurra.glasciia.impl.LoadFile

/**
  * Created by johan_home on 2016-10-15.
  */
case class FilePixmapLoader() extends Loader[Pixmap] with Logging {
  override def get(fileName: String, flush: Boolean): Option[Pixmap] = {
    LoadFile(fileName) match {
      case r @ Some(fileHandle) =>
        val rawPixmap = new Pixmap(fileHandle)
        if (rawPixmap.getFormat == Pixmap.Format.RGBA8888) {
          Some(rawPixmap)
        } else {
          log.info(s"WARNING: Loading $fileName - which has format ${rawPixmap.getFormat}. Converting on Cpu... Please re-encode to Pixmap.Format.RGBA8888 for faster load times")
          val convertedPixmap = new Pixmap(rawPixmap.getWidth, rawPixmap.getHeight, Pixmap.Format.RGBA8888)
          val blend = Pixmap.getBlending
          Pixmap.setBlending(Pixmap.Blending.None)
          convertedPixmap.drawPixmap(rawPixmap, 0, 0, 0, 0, rawPixmap.getWidth, rawPixmap.getHeight)
          Pixmap.setBlending(blend)
          rawPixmap.dispose()
          Some(convertedPixmap)
        }
      case None => None
    }
  }
  override def flush(force: Boolean): Unit = {}

  override def dispose(): Unit = {}

  override def remove(name: String): Unit = {}
}
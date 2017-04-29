package com.github.gigurra.glasciia

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Pixmap
import com.github.gigurra.glasciia.impl.LoadFile

/**
  * Created by johan_home on 2016-10-15.
  */
case class FilePixmapLoader() extends Loader[Pixmap] with Logging {

  def apply(fileHandle: FileHandle, flush: Boolean): Pixmap = {
    val rawPixMap = new Pixmap(fileHandle)
    if (rawPixMap.getFormat == Pixmap.Format.RGBA8888) {
      rawPixMap
    } else {
      log.info(s"WARNING: Loading $fileHandle - which has format ${rawPixMap.getFormat}. Converting on Cpu... Please re-encode to Pixmap.Format.RGBA8888 for faster load times")
      val convertedPixmap = new Pixmap(rawPixMap.getWidth, rawPixMap.getHeight, Pixmap.Format.RGBA8888)
      val blend = convertedPixmap.getBlending
      convertedPixmap.setBlending(Pixmap.Blending.None)
      convertedPixmap.drawPixmap(rawPixMap, 0, 0, 0, 0, rawPixMap.getWidth, rawPixMap.getHeight)
      convertedPixmap.setBlending(blend)
      rawPixMap.dispose()
      convertedPixmap
    }
  }

  override def get(fileName: String, flush: Boolean): Option[Pixmap] = {
    LoadFile(fileName).map(apply(_, flush))
  }

  override def flush(force: Boolean): Unit = {}
  override def dispose(): Unit = {}
  override def remove(name: String): Unit = {}
}
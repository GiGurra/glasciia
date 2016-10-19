package com.github.gigurra.glasciia

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.github.gigurra.glasciia.impl.LoadFile

/**
  * Created by johan_home on 2016-10-15.
  */
case class FileTextureRegionLoader(conf: TextureRegionLoader.Conf) extends Loader[TextureRegion] {
  override def get(fileName: String, upload: Boolean = false): Option[TextureRegion] = {
    LoadFile(fileName) match {
      case r @ Some(fileHandle) =>
        Some(StaticImage.fromFile(
          fileHandle = fileHandle,
          useMipMaps = conf.useMipMaps,
          minFilter = conf.minFilter,
          magFilter = conf.magFilter
        ))
      case None => None
    }
  }

  override def uploadIfDirty(): Unit = {}
}
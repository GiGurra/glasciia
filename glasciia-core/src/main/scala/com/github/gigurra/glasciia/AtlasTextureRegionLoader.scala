package com.github.gigurra.glasciia

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.github.gigurra.glasciia.impl.DynamicTextureAtlas
import com.github.gigurra.math.Vec2

/**
  * Created by johan_home on 2016-10-15.
  */
case class AtlasTextureRegionLoader(atlas: DynamicTextureAtlas, fallback: Option[Loader[Pixmap]] = None) extends Loader[TextureRegion] {

  override def get(name: String, flush: Boolean = false): Option[TextureRegion] = {
    val nameWithoutFileEnding = stripEnding(name)
    atlas.get(nameWithoutFileEnding) match {
      case r @ Some(_) => r
      case None => fallback.flatMap(_.get(name, flush)) match {
        case Some(source) => Some(atlas.add(nameWithoutFileEnding, source, rebuildMipmaps = flush, deleteSource = true))
        case None => None
      }
    }
  }

  def reserve(name: String, width: Int, height: Int): TextureRegion = {
    atlas.reserve(name, width, height)._1
  }

  def pageSize: Vec2 = {
    atlas.pageSize
  }

  private def stripEnding(name: String): String = {
    name.lastIndexOf('.') match {
      case -1 => name
      case i => name.splitAt(i)._1
    }
  }

  override def flush(force: Boolean = false): Unit = {
    atlas.rebuildMipmaps(force = force)
  }

  override def dispose(): Unit = {
    atlas.dispose()
  }
}

package com.github.gigurra.glasciia

import com.badlogic.gdx.graphics.{Camera, Pixmap}
import com.badlogic.gdx.graphics.g2d.{SpriteBatcher, TextureRegion}
import com.github.gigurra.glasciia.impl.DynamicTextureAtlas
import com.github.gigurra.glasciia.impl.DynamicTextureAtlas.AtlasRegion
import com.github.gigurra.math.Vec2

/**
  * Created by johan_home on 2016-10-15.
  */
case class AtlasTextureRegionLoader(atlas: DynamicTextureAtlas, fallback: Option[Loader[Pixmap]] = None) extends Loader[TextureRegion] {

  override def get(name: String, flush: Boolean = false): Option[TextureRegion] = {
    atlas.get(name) match {
      case r @ Some(_) => r
      case None => fallback.flatMap(_.get(name, flush)) match {
        case Some(source) => Some(atlas.add(name, source, flush = flush, deleteSource = true))
        case None => None
      }
    }
  }

  def reserve(name: String, width: Int, height: Int): AtlasRegion = {
    atlas.reserve(name, width, height)
  }

  def paint(region: AtlasRegion,
            batch: SpriteBatcher,
            projection: Camera,
            clear: Boolean = true)(content: => Unit): Unit = {
    atlas.paint(
      region,
      batch,
      projection,
      clear
    )(content)
  }

  def remove(name: String): Unit = {
    atlas.remove(name)
  }

  def pageSize: Vec2 = {
    atlas.pageSize
  }

  override def flush(force: Boolean = false): Unit = {
    atlas.flush(force = force)
  }

  override def dispose(): Unit = {
    atlas.clear()
  }
}

package com.github.gigurra.glasciia

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.{Camera, Pixmap}
import com.badlogic.gdx.graphics.g2d.{SpriteBatcher, TextureRegion}
import com.github.gigurra.glasciia.impl.DynamicTextureAtlas
import com.github.gigurra.glasciia.impl.DynamicTextureAtlas.AtlasRegion
import com.github.gigurra.math.Vec2

import scala.collection.mutable

/**
  * Created by johan_home on 2016-10-15.
  */
case class AtlasTextureRegionLoader(atlas: DynamicTextureAtlas = DynamicTextureAtlas()) extends Loader[TextureRegion] {

  private val aliases = mutable.HashMap[String, String]()
  private val filePixmapLoader = FilePixmapLoader()

  override def get(name: String, flush: Boolean = false): Option[TextureRegion] = {
    atlas.get(aliases.getOrElse(name, name))
  }

  def getOrElseLoadFromFile(name: String, flush: Boolean = false): TextureRegion = {
    get(name, flush) match {
      case Some(region) => region
      case None => add(name, filePixmapLoader(name, flush), deleteSource = true, flush = flush)
    }
  }

  def add(name: String, source: Pixmap, deleteSource: Boolean, flush: Boolean = false): AtlasRegion = {
    atlas.add(name, source, deleteSource = deleteSource, flush = flush)
  }

  def load(name: String, fileHandle: FileHandle, flush: Boolean = false): AtlasRegion = {
    add(name, filePixmapLoader(fileHandle, flush = flush), deleteSource = true, flush = flush)
  }

  def addAlias(alias: String, name: String): Unit = {
    aliases.put(alias, name)
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

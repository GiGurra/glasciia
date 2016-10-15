package se.gigurra.glasciia

import com.badlogic.gdx.graphics.g2d.TextureRegion
import se.gigurra.glasciia.impl.DynamicTextureAtlas

/**
  * Created by johan_home on 2016-10-15.
  */
case class AtlasTextureRegionLoader(atlas: DynamicTextureAtlas, fallback: Option[Loader[TextureRegion]] = None) extends Loader[TextureRegion] {
  override def get(name: String, upload: Boolean = false): Option[TextureRegion] = {
    val nameWithoutFileEnding = stripEnding(name)
    atlas.get(nameWithoutFileEnding) match {
      case r @ Some(region) => r
      case None => fallback.flatMap(_.get(name, upload)) match {
        case Some(source) => Some(atlas.add(nameWithoutFileEnding, source, upload = upload, deleteSource = true))
        case None => None
      }
    }
  }

  private def stripEnding(name: String): String = {
    name.lastIndexOf('.') match {
      case -1 => name
      case i => name.splitAt(i)._1
    }
  }

  override def uploadIfDirty(): Unit = atlas.uploadIfDirty()
}

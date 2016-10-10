package se.gigurra.glasciia

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import se.gigurra.glasciia.impl.{DynamicTextureAtlas, LoadFile}

/**
  * Created by johan on 2016-10-08.
  */
object TextureRegionLoader {

  case class Conf(useMipMaps: Boolean = true,
                  minFilter: Texture.TextureFilter = Texture.TextureFilter.MipMapLinearLinear,
                  magFilter: Texture.TextureFilter = Texture.TextureFilter.Linear)

  def createNew(conf: Conf = Conf())(atlas: DynamicTextureAtlas = new DynamicTextureAtlas(conf)): Loader.InMemory[TextureRegion] = {
    Loader.InMemory[TextureRegion](
      fallback = Some(FromAtlas(atlas,
        fallback = Some(FromFiles(conf))
      ))
    )
  }

  case class FromAtlas(atlas: DynamicTextureAtlas, fallback: Option[Loader[TextureRegion]] = None) extends Loader[TextureRegion] {
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

  case class FromFiles(conf: Conf) extends Loader[TextureRegion] {
    override def get(fileName: String, upload: Boolean = false): Option[TextureRegion] = {
      LoadFile(fileName) match {
        case r @ Some(fileHandle) =>
          Some(StaticImage.fromFile(
            fileHandle = fileHandle,
            useMipMaps = conf.useMipMaps,
            minFilter = conf.minFilter,
            magFilter = conf.magFilter
          ).region)
        case None => None
      }
    }

    override def uploadIfDirty(): Unit = {}
  }
}

package se.gigurra.glasciia

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.{TextureAtlas, TextureRegion}
import se.gigurra.glasciia.impl.LoadFile

/**
  * Created by johan on 2016-10-08.
  */
object TextureRegionLoader {

  case class Conf(useMipMaps: Boolean = true,
                  minFilter: Texture.TextureFilter = Texture.TextureFilter.MipMapLinearLinear,
                  magFilter: Texture.TextureFilter = Texture.TextureFilter.Linear)

  def createNew(atlas: TextureAtlas = new TextureAtlas(), conf: Conf = Conf()): Loader.InMemory[TextureRegion] = {
    Loader.InMemory[TextureRegion](
      fallback = Some(FromAtlas(atlas,
        fallback = Some(FromFiles(conf))
      ))
    )
  }

  case class FromAtlas(atlas: TextureAtlas, fallback: Option[Loader[TextureRegion]] = None) extends Loader[TextureRegion] {
    override def get(name: String): Option[TextureRegion] = {
      val nameWithoutFileEnding = stripEnding(name)
      Option(atlas.findRegion(nameWithoutFileEnding)) match {
        case r @ Some(region) => r
        case None => fallback.flatMap(_.get(name)) match {
          case Some(region) => Some(atlas.addRegion(nameWithoutFileEnding, region)) // TODO: Packing. If configured to do so..
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
  }

  case class FromFiles(conf: Conf) extends Loader[TextureRegion] {
    override def get(fileName: String): Option[TextureRegion] = {
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
  }
}

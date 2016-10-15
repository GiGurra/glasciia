package se.gigurra.glasciia

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import se.gigurra.glasciia.impl.DynamicTextureAtlas

/**
  * Created by johan on 2016-10-08.
  */
object TextureRegionLoader {

  case class Conf(useMipMaps: Boolean = true,
                  minFilter: Texture.TextureFilter = Texture.TextureFilter.MipMapLinearLinear,
                  magFilter: Texture.TextureFilter = Texture.TextureFilter.Linear)

  def newDefault(conf: Conf = Conf())(atlas: DynamicTextureAtlas = new DynamicTextureAtlas(conf)): InMemoryLoader[TextureRegion] = {
    InMemoryLoader[TextureRegion](
      fallback = Some(AtlasTextureRegionLoader(atlas,
        fallback = Some(FileTextureRegionLoader(conf))
      ))
    )
  }
}

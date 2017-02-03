package com.github.gigurra.glasciia

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.github.gigurra.glasciia.impl.DynamicTextureAtlas

/**
  * Created by johan on 2016-10-08.
  */
object TextureRegionLoader {

  def newDefault(conf: TextureConf = TextureConf())(atlas: DynamicTextureAtlas = new DynamicTextureAtlas(conf)): InMemoryLoader[TextureRegion] = {
    InMemoryLoader[TextureRegion](
      impl = AtlasTextureRegionLoader(atlas,
        fallback = Some(FilePixmapLoader())
      )
    )
  }
}

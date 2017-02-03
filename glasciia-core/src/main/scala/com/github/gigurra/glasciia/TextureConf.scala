package com.github.gigurra.glasciia

import com.badlogic.gdx.graphics.Texture

/**
  * Created by johan on 2017-02-03.
  */
case class TextureConf(useMipMaps: Boolean = true,
                       minFilter: Texture.TextureFilter = Texture.TextureFilter.MipMapLinearLinear,
                       magFilter: Texture.TextureFilter = Texture.TextureFilter.Linear)


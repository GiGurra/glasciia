package com.badlogic.gdx.graphics

import com.badlogic.gdx.Gdx

/**
  * Created by johan_home on 2016-10-15.
  */
object TextureAccess {

  /**
    * LibGdx made access level to glHandle and delete protected.. so this is what we gotta do :S
    * This function should be called when the Resume event is received
    * @param texture the texture to reload
    */
  def reloadOnContextLoss(texture: Texture): Unit = {
    texture.glHandle = Gdx.gl.glGenTexture()
    texture.load(texture.data)
  }
}

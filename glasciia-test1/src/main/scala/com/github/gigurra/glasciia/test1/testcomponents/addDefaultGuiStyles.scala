package com.github.gigurra.glasciia.test1.testcomponents

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.{BitmapFont, TextureRegion}
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.github.gigurra.glasciia.Glasciia._
import com.github.gigurra.glasciia.{AtlasTextureRegionLoader, ResourceManager}

/**
  * Created by johan on 2016-10-09.
  */
object addDefaultGuiStyles {

  def apply(resources: ResourceManager, skin: Skin, regions: AtlasTextureRegionLoader): Unit = {
    skin
      .addStyle[TextureRegion]("fill", regions("filled-texture"))
      .addStyle[BitmapFont](resources[BitmapFont]("font:monospace-default"))
      .addStyle[BitmapFont]("masked-font", resources[BitmapFont]("font:monospace-default-masked"))
      .addStyle[TextButtonStyle](new TextButtonStyle {
        val standard = skin.newInstance("fill", Color.DARK_GRAY)
        val highlighted = skin.newInstance("fill", Color.LIGHT_GRAY)
        up = standard
        down = standard
        over = highlighted
        font = skin.style[BitmapFont]
      })
      .addStyle[TextButtonStyle]("default:keyboard-focus", new TextButtonStyle(skin.style[TextButtonStyle]) {
        val kbFocus = skin.newInstance("fill", Color.LIME)
        up = kbFocus
        down = kbFocus
      })
      .addStyle[LabelStyle](new LabelStyle {
        font = skin.style[BitmapFont]
        fontColor = Color.CHARTREUSE
      })
  }
}

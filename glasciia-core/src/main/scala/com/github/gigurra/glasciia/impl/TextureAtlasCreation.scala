package com.github.gigurra.glasciia.impl

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.tools.texturepacker.TexturePacker
import com.badlogic.gdx.utils.Json

/**
  * Created by johan on 2016-10-04.
  */
trait TextureAtlasCreation {

  def readTexturePackSettings(file: FileHandle): TexturePacker.Settings = {
    val inputStream = file.read()
    try {
      new Json().fromJson(classOf[TexturePacker.Settings], inputStream)
    } finally {
      inputStream.close()
    }
  }

  def packFilesIntoTextureAtlas(settings: TexturePacker.Settings,
                                inputDir: String,
                                outputDir: String,
                                outputIndexFile: String): Unit = {
    TexturePacker.process(
      settings,
      inputDir,
      outputDir,
      outputIndexFile
    )
  }
}

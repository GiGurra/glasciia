package se.gigurra.glasciia.test1.testcomponents

import java.io.File

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import se.gigurra.glasciia.App
import se.gigurra.glasciia.Glasciia._
import se.gigurra.glasciia.impl.LoadFile

/**
  * Created by johan on 2016-10-08.
  */
object buildAndLoadTextureAtlas {

  def apply(app: App): Unit = {

    val texturePackSettings = readTexturePackSettings("test-atlast-cfg.json")
    val inputFolder = new File(getClass.getClassLoader.getResource("test-atlast-cfg.json").getFile).getAbsoluteFile.getParent
    val outputFolder = LoadFile("target/").get.file().getAbsolutePath

    packFilesIntoTextureAtlas(texturePackSettings, inputDir = inputFolder, outputDir = outputFolder, "test-atlast.atlas")

    println(s"$inputFolder -> $outputFolder")

    app.addResource("texture-atlas", new TextureAtlas(s"$outputFolder/test-atlast.atlas", outputFolder))

  }
}

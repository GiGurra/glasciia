package com.github.gigurra.glasciia.test1

import com.badlogic.gdx.backends.lwjgl.{LwjglApplication, LwjglApplicationConfiguration}
import com.github.gigurra.glasciia.GameLauncher._

/**
  * Created by johan on 2016-09-26.
  */
object Main {

  def main(args: Array[String]): Unit = {

    val config = new LwjglApplicationConfiguration {
      x = 100
      y = 100
      width = 640
      height = 480
      resizable = true
      fullscreen = false
      title = "Test Game 1"
      forceExit = false
      vSyncEnabled = true
      samples = 4
      foregroundFPS = 0 // 0 means no limit
      backgroundFPS = 30
    }

    new LwjglApplication(TestGame, config)
  }
}

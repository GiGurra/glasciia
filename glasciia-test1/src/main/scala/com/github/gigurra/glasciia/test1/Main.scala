package com.github.gigurra.glasciia.test1

import com.badlogic.gdx.backends.lwjgl.{LwjglApplication, LwjglApplicationConfiguration}
import com.github.gigurra.glasciia.test1.testcomponents.TestGameResources
import com.github.gigurra.glasciia._

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

    new LwjglApplication(GameLauncher(new GameFactory[TestGameResources, Canvas] {
      override def canvas(): Canvas = new Canvas
      override def loadingScreen(canvas: Canvas): Game = new LoadingScreen(canvas)
      override def launch(resources: TestGameResources, canvas: Canvas): Game = new TestGame(resources, canvas)
      override def resources(canvas: Canvas): TestGameResources = new TestGameResources(canvas)
    }), config)
  }
}

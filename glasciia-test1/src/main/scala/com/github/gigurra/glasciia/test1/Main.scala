package com.github.gigurra.glasciia.test1

import com.badlogic.gdx.backends.lwjgl.{LwjglApplication, LwjglApplicationConfiguration}
import com.github.gigurra.glasciia.{Game, GameLauncher, GameLauncherIfc, Resources}

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

    new LwjglApplication(GameLauncher(new GameLauncherIfc[Resources] {
      override def loadingScreen(): Game = new LoadingScreen()
      override def launch(resources: Resources): Game = new TestGame
      override def resources(): Resources = new Resources {
        private def time = System.nanoTime()/1e9
        private val t0 = time
        /**
          * @return true if done
          */
        override protected def loadSome() = {
          time - t0 > 3.0f
        }
      }
    }), config)
  }
}

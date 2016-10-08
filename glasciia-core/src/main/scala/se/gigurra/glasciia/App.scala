package se.gigurra.glasciia

import se.gigurra.glasciia.App.{GlConf, WindowConf}
import se.gigurra.glasciia.impl.{ApplicationEventListener, ResourceManager}
import se.gigurra.math.Vec2

import scala.util.control.NonFatal

/**
  * Created by johan on 2016-09-19.
  */
abstract class App(val initialWindowConf: WindowConf,
                   val initialGlConf: GlConf) extends ApplicationEventListener with ResourceManager {

  private val t0 = System.nanoTime / 1e9

  def width: Int
  def height: Int
  def size: Vec2[Int] = Vec2(width, height)
  def localAppTime: Double = System.nanoTime / 1e9 - t0
}

object App {

  def defaultCrashHandler(err: Throwable): Unit = {
    err match {
      case NonFatal(e) =>
        err.printStackTrace(System.err)
        System.exit(1)
      case e =>
        System.err.println(s"Fatal exception, Logging failure.. OOM?. Attempting stack trace print..\n")
        err.printStackTrace(System.err)
        System.exit(2)
    }
  }

  case class GlConf(vsync: Boolean = true,
                    msaa: Int = 4,
                    foregroundFpsCap: Option[Int] = None,
                    backgroundFpsCap: Option[Int] = Some(30)) {

  }

  case class WindowConf(position: Vec2[Int],
                        size: Vec2[Int],
                        resizable: Boolean,
                        maximized: Boolean,
                        fullscreen: Boolean,
                        title: String)
}

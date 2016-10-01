package se.gigurra.glasciia

import se.gigurra.glasciia.conf.{CameraConf, GlConf, WindowConf}
import se.gigurra.math.Vec2

import scala.util.control.NonFatal

/**
  * Created by johan on 2016-09-19.
  */
abstract class App(val initialWindowConf: WindowConf,
                   val initialCameraConf: CameraConf,
                   val initialGlConf: GlConf) {

  def width: Int
  def height: Int
  def size: Vec2[Int] = Vec2(width, height)
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
}

package se.gigurra.glasciia

import se.gigurra.glasciia.conf.{CameraConf, GlConf, WindowConf}
import scala.util.control.NonFatal

/**
  * Created by johan on 2016-09-19.
  */
abstract class Window(val initialWindowConf: WindowConf,
                      val initialCameraConf: CameraConf,
                      val initialGlConf: GlConf)

object Window {
  def defaultCrashLogger(err: Throwable): Unit = {
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

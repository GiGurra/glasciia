package se.gigurra.glasciia

import rx.lang.scala.Observable
import se.gigurra.lang.ScopedSet

import scala.util.control.NonFatal

/**
  * Created by johan on 2016-09-19.
  */
abstract class Window(val initialWindowConf: WindowConf,
                      val initialCameraConf: CameraConf) {

  //////////////////////
  // Public API

  final def withBackground(color: Color)(content: => Unit): Unit = ScopedSet(background, setBackgroundColor, color)(content)
  final def withForeground(color: Color)(content: => Unit): Unit = ScopedSet(foreground, setForegroundColor, color)(content)
  final def withCamera(cameraConf: CameraConf)(content: => Unit): Unit = ScopedSet(_cameraConf, setCamera, cameraConf)(content)

  def draw(c: Char,
           size: Float,
           bold: Boolean = false,
           italic: Boolean = false,
           foreground: Color = foreground,
           background: Color = background): Unit

  def events: Observable[ApplicationEvent]

  def close(): Unit

  def handleEvents(f: ApplicationEvent => Unit): Unit = {
    events.foreach(f, Window.defaultCrashLogger)
  }

  //////////////////////
  // For implementations

  protected final def camera: CameraConf = _cameraConf


  //////////////////////
  // Private Helpers

  private var background = Color.BLACK
  private var foreground = Color.WHITE
  private var _cameraConf = initialCameraConf

  private def setCamera(cameraConf: CameraConf): Unit = {
    _cameraConf = cameraConf
  }

  private def setBackgroundColor(color: Color): Unit = background = color
  private def setForegroundColor(color: Color): Unit = foreground = color

}

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
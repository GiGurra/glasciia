package se.gigurra.glasciia

import se.gigurra.glasciia.helpers.{Scoped, ScopedSet}

/**
  * Created by johan on 2016-09-19.
  */
abstract class Window(val initialWindowConf: WindowConf,
                      val initialCameraConf: CameraConf) {

  //////////////////////
  // Public API

  final def drawFrame(cameraConf: CameraConf = _cameraConf)(content: => Unit): Unit = Scoped(beginFrame(cameraConf), content, endFrame())
  final def withBackground(color: Color)(content: => Unit): Unit = ScopedSet(background, setBackgroundColor, color)(content)
  final def withForeground(color: Color)(content: => Unit): Unit = ScopedSet(foreground, setForegroundColor, color)(content)

  final def withCamera(cameraConf: CameraConf)(content: => Unit): Unit = ScopedSet(_cameraConf, setCamera, cameraConf)(content)

  def draw(c: Char,
           size: Float,
           bold: Boolean = false,
           italic: Boolean = false,
           foreground: Color = foreground,
           background: Color = background): Unit


  //////////////////////
  // For implementations

  protected def doBeginFrame(): Unit = {}
  protected def doClearScreen(): Unit = {}
  protected def doSetCamera(): Unit = {}
  protected def doEndFrame(): Unit = {}
  protected final def camera: CameraConf = _cameraConf


  //////////////////////
  // Private Helpers

  private var background = Color.BLACK
  private var foreground = Color.WHITE
  private var _cameraConf = initialCameraConf

  private def setCamera(cameraConf: CameraConf): Unit = {
    _cameraConf = cameraConf
    doSetCamera()
  }

  private def setBackgroundColor(color: Color): Unit = background = color
  private def setForegroundColor(color: Color): Unit = foreground = color

  private def beginFrame(cameraConf: CameraConf): Unit = {
    doBeginFrame()
    doSetCamera()
    doClearScreen()
  }

  private def endFrame(): Unit = {
    doEndFrame()
  }

}

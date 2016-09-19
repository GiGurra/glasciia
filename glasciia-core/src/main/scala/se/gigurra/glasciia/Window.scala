package se.gigurra.glasciia

import se.gigurra.glasciia.helpers.{Scoped, ScopedSet}

/**
  * Created by johan on 2016-09-19.
  */
class Window(val windowConf: WindowConf,
             val renderConf: RenderConf) {

  //////////////////////
  // Public API

  def drawFrame(at: Vec2[Float], conf: RenderConf = renderConf)(content: => Unit): Unit = Scoped(beginFrame(at, conf), content, endFrame())
  def withBackground(color: Color)(content: => Unit): Unit = ScopedSet(background, setBackgroundColor, color)(content)
  def withForeground(color: Color)(content: => Unit): Unit = ScopedSet(foreground, setForegroundColor, color)(content)

  def withCameraPos(pos: Vec2[Float])(content: => Unit): Unit = ScopedSet(cameraPos, setCameraPos, pos)(content)

  def draw(c: Char,
           bold: Boolean = false,
           italic: Boolean = false,
           foreground: Color = foreground,
           background: Color = background,
           size: Float = renderConf.characterSize): Unit = {
    ??? // TODO: Some impl
  }


  //////////////////////
  // Private Helpers

  private var background = Color.BLACK
  private var foreground = Color.WHITE
  private var cameraPos = Vec2[Float](0.0f, 0.0f)

  private def setCameraPos(pos: Vec2[Float]): Unit = cameraPos = pos
  private def setBackgroundColor(color: Color): Unit = background = color
  private def setForegroundColor(color: Color): Unit = foreground = color

  private def beginFrame(at: Vec2[Float], renderConf: RenderConf): Unit = {
    // TODO: Clear screen
  }

  private def endFrame(): Unit = {
    // TODO: draw everything queued
  }

}

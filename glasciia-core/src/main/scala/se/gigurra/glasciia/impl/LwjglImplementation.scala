package se.gigurra.glasciia.impl

import java.util.concurrent.TimeUnit

import com.badlogic.gdx.backends.lwjgl.{LwjglApplication, LwjglApplicationConfiguration}
import com.badlogic.gdx.{ApplicationListener, Gdx, InputProcessor}
import org.lwjgl.opengl.Display
import se.gigurra.glasciia.App

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Promise}

/**
  * Created by johan on 2016-09-26.
  */
trait LwjglImplementation { self: App =>

  def close(): Unit = lwjglApplication.exit()
  def width: Int = lwjglApplication.getGraphics.getWidth
  def height: Int = lwjglApplication.getGraphics.getHeight

  /////////////////////////////////////////////
  // Expectations

  protected def appListener: ApplicationListener
  protected def inputListener: InputProcessor


  /////////////////////////////////////////////
  // Implemented expectations

  def isOnRenderThread: Boolean = Display.isCurrent


  /////////////////////////////////////////////
  // Private

  import initialGlConf._

  private val lwjglConf = new LwjglApplicationConfiguration {
    title = s"${initialWindowConf.title}"
    x = initialWindowConf.position.x
    y = initialWindowConf.position.y
    width = initialWindowConf.size.x
    height = initialWindowConf.size.y
    foregroundFPS = foregroundFpsCap.getOrElse(0)
    backgroundFPS = backgroundFpsCap.getOrElse(0)
    forceExit = true
    vSyncEnabled = vsync
    samples = msaa
    resizable = initialWindowConf.resizable
    fullscreen = initialWindowConf.fullscreen
  }
  private val startup = Promise[Unit]()
  private val lwjglApplication = new LwjglApplication(new ApplicationListener {
    override def resize(width: Int, height: Int): Unit = appListener.resize(width, height)
    override def dispose(): Unit = appListener.dispose()
    override def pause(): Unit = appListener.pause()
    override def render(): Unit = appListener.render()
    override def resume(): Unit = appListener.resume()
    override def create(): Unit = { startup.success(()); appListener.create() }
  }, lwjglConf)

  Gdx.input.setInputProcessor(inputListener)
  Await.result(startup.future, Duration(30, TimeUnit.SECONDS))
}

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

  def close(): Unit = lwjglApplication.stop()

  ///////////////////////////
  // startup sequence below

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
  }

  protected def appListener: ApplicationListener
  protected def inputListener: InputProcessor

  private val initFuture = Promise[Unit]()
  private val lwjglApplication = new LwjglApplication(appListener, lwjglConf)
  Gdx.input.setInputProcessor(inputListener)

  override def width: Int = lwjglApplication.getGraphics.getWidth
  override def height: Int = lwjglApplication.getGraphics.getHeight

  protected def setCreated(): Unit = {
    initFuture.success(())
  }

  def isOnRenderThread: Boolean = Display.isCurrent

  Await.result(initFuture.future, Duration(30, TimeUnit.SECONDS))
}

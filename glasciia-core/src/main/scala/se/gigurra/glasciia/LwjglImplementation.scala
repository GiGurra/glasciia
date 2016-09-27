package se.gigurra.glasciia

import com.badlogic.gdx.backends.lwjgl.{LwjglApplication, LwjglApplicationConfiguration}
import com.badlogic.gdx.{ApplicationListener, Gdx, InputProcessor}
import rx.lang.scala.Subject
import se.gigurra.glasciia.ApplicationEvent._
import se.gigurra.math.Vec2

/**
  * Created by johan on 2016-09-26.
  */
trait LwjglImplementation { _: GdxWindow =>

  override def events = subject
  override def close(): Unit = lwjglApplication.stop()


  ///////////////////////////
  // startup sequence below

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

  private val subject = Subject[ApplicationEvent]().toSerialized
  subject.subscribe()

  private def consume(ev: ApplicationEvent): Boolean = {
    subject.onNext(ev)
    true
  }

  private val appListener = new ApplicationListener {
    override def resize(width: Int, height: Int): Unit = consume(Resize(Vec2(width, height)))
    override def dispose(): Unit = consume(Exit)
    override def pause(): Unit = consume(Pause)
    override def render(): Unit = consume(Render)
    override def resume(): Unit = consume(Resume)
    override def create(): Unit = consume(Init)
  }

  private val inputListener = new InputProcessor {
    override def keyTyped(character: Char): Boolean = consume(CharTyped(character))
    override def keyDown(keycode: Int): Boolean = consume(KeyDown(keycode))
    override def keyUp(keycode: Int): Boolean = consume(KeyUp(keycode))
    override def mouseMoved(screenX: Int, screenY: Int): Boolean = consume(MouseMove(Vec2(screenX, screenY)))
    override def scrolled(amount: Int): Boolean = consume(MouseScrolled(amount))
    override def touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = consume(TouchDown(Vec2(screenX, screenY), pointer, button))
    override def touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = consume(TouchUp(Vec2(screenX, screenY), pointer, button))
    override def touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean = consume(TouchDrag(Vec2(screenX, screenY), pointer))
  }

  private val lwjglApplication = new LwjglApplication(appListener, lwjglConf)
  Gdx.input.setInputProcessor(inputListener)

}

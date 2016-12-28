package com.github.gigurra.glasciia

import com.badlogic.gdx.Gdx

import scala.collection.concurrent.TrieMap

/**
  * Created by johan on 2016-12-03.
  */
trait Logging {
  protected lazy val log: Logger = Logger.getLogger(this.getClass)
}

object Logging {

  import com.badlogic.gdx.Application

  val NONE = Application.LOG_NONE
  val ERROR = Application.LOG_ERROR
  val INFO = Application.LOG_INFO
  val DEBUG = Application.LOG_DEBUG

  def setGlobalLevel(level: Int): Unit = {
    backends.foreach(_.setLogLevel(level))
  }

  def overrideBackend(backend: Backend): Unit = {
    this.backends = Vector(backend)
  }

  def addBackend(backend: Backend): Unit = {
    this.backends :+= backend
  }

  @volatile private[glasciia] var backends: Vector[Backend] = Vector(GdxLoggingBackend)

  trait Backend {
    def setLogLevel(level: Int): Unit
    def error(s: String, msg: String): Unit
    def error(s: String, msg: String, exc: Throwable): Unit
    def log(s: String, msg: String): Unit
    def debug(s: String, msg: String): Unit
    def globalLogLevel: Int
  }

  object GdxLoggingBackend extends Backend {
    override def error(s: String, msg: String, exc: Throwable): Unit = Gdx.app.error(s, msg, exc)
    override def error(s: String, msg: String): Unit = Gdx.app.error(s, msg)
    override def log(s: String, msg: String): Unit = Gdx.app.log(s, msg)
    override def debug(s: String, msg: String): Unit = Gdx.app.log(s, msg)
    override def globalLogLevel: Int = Gdx.app.getLogLevel
    override def setLogLevel(level: Int): Unit = Gdx.app.setLogLevel(level)
  }

  object SystemOutBackend extends Backend {
    @volatile private var _globalLogLevel: Int = INFO

    override def error(s: String, msg: String, exc: Throwable): Unit = {
      System.out.println(s + ": " + msg)
      exc.printStackTrace(System.out)
    }
    override def error(s: String, msg: String): Unit = System.out.println(s + ": " + msg)
    override def log(s: String, msg: String): Unit = System.out.println(s + ": " + msg)
    override def debug(s: String, msg: String): Unit = System.out.println(s + ": " + msg)
    override def globalLogLevel: Int = _globalLogLevel
    override def setLogLevel(level: Int): Unit = _globalLogLevel = level
  }

  object SystemErrBackend extends Backend {
    @volatile private var _globalLogLevel: Int = INFO

    override def error(s: String, msg: String, exc: Throwable): Unit = {
      System.err.println(s + ": " + msg)
      exc.printStackTrace(System.err)
    }
    override def error(s: String, msg: String): Unit = System.err.println(s + ": " + msg)
    override def log(s: String, msg: String): Unit = System.err.println(s + ": " + msg)
    override def debug(s: String, msg: String): Unit = System.err.println(s + ": " + msg)
    override def globalLogLevel: Int = _globalLogLevel
    override def setLogLevel(level: Int): Unit = _globalLogLevel = level
  }

  object SystemOutErrBackend extends Backend {

    @volatile private var _globalLogLevel: Int = INFO

    override def error(s: String, msg: String, exc: Throwable): Unit = {
      System.err.println(s + ": " + msg)
      exc.printStackTrace(System.err)
    }
    override def error(s: String, msg: String): Unit = System.err.println(s + ": " + msg)
    override def log(s: String, msg: String): Unit = System.out.println(s + ": " + msg)
    override def debug(s: String, msg: String): Unit = System.out.println(s + ": " + msg)
    override def globalLogLevel: Int = _globalLogLevel
    override def setLogLevel(level: Int): Unit = _globalLogLevel = level
  }
}

class Logger(cls: Class[_]) {
  @volatile private var _level: Int = Logging.INFO

  def debug(msg: => String): Unit = {
    if (level >= Logging.DEBUG) {
      Logging.backends.foreach(_.debug(prefix("DEBUG"), msg))
    }
  }

  def info(msg: => String): Unit = {
    if (level >= Logging.INFO) {
      Logging.backends.foreach(_.log(prefix("INFO"), msg))
    }
  }

  def error(msg: => String, source: Option[Throwable] = None): Unit = {
    if (level >= Logging.ERROR) {
      source match {
        case Some(exc)  => Logging.backends.foreach(_.error(prefix("ERROR"), msg, exc))
        case None       => Logging.backends.foreach(_.error(prefix("ERROR"), msg))
      }
    }
  }

  def level: Int = {
    _level
  }

  def setLevel(newLevel: Int): Unit = {
    _level = newLevel
  }

  private def prefix(level: String): String = {
    s"[$level] $timeStamp ${cls.getName}"
  }

  private def timeStamp: String = {
    val df = new java.text.SimpleDateFormat("yyyy:MM:dd-HH:mm:ss:SSS")
    df.format(new java.util.Date())
  }
}

object Logger {

  private val loggers = new TrieMap[Class[_], Logger]

  def getLogger(cls: Class[_]): Logger = {
    loggers.getOrElseUpdate(cls, new Logger(cls))
  }
}

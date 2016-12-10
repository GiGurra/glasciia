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
    backend.setLogLevel(level)
  }

  // Below are mostly used for tests
  def overrideBackend(backend: Backend): Unit = {
    this.backend = backend
  }

  @volatile private[glasciia] var backend: Backend = GdxLoggingBackend

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

  object TestLoggingBackend extends Backend {

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
  @volatile private var _localLogLevel: Int = Logging.INFO

  def debug(msg: => String): Unit = {
    if (level >= Logging.DEBUG) {
      Logging.backend.debug(prefix("DEBUG"), msg)
    }
  }

  def info(msg: => String): Unit = {
    if (level >= Logging.INFO) {
      Logging.backend.log(prefix("INFO"), msg)
    }
  }

  def error(msg: => String, source: Option[Throwable] = None): Unit = {
    if (level >= Logging.ERROR) {
      source match {
        case Some(exc) => Logging.backend.error(prefix("ERROR"), msg, exc)
        case None => Logging.backend.error(prefix("ERROR"), msg)
      }
    }
  }

  def globalLevel: Int = {
    Logging.backend.globalLogLevel
  }

  def localLevel: Int = {
    _localLogLevel
  }

  def level: Int = {
    math.min(globalLevel, localLevel)
  }

  def setLocalLevel(newLevel: Int): Unit = {
    _localLogLevel = newLevel
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

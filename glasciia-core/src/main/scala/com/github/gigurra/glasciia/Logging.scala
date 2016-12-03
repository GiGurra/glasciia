package com.github.gigurra.glasciia

import com.badlogic.gdx.Gdx

/**
  * Created by johan on 2016-12-03.
  */
trait Logging {
  lazy val log = Logger(this)
}

object Logging {
  import com.badlogic.gdx.Application
  val NONE = Application.LOG_NONE
  val ERROR = Application.LOG_ERROR
  val INFO = Application.LOG_INFO
  val DEBUG = Application.LOG_DEBUG

  def setGlobalLevel(level: Int): Unit = {
    Gdx.app.setLogLevel(level)
  }
}

case class Logger(origin: AnyRef) {
  private val cls = origin.getClass
  @volatile private var _localLogLevel: Int = Gdx.app.getLogLevel

  def debug(msg: => String): Unit = {
    if (level >= Logging.DEBUG) {
      Gdx.app.debug(prefix("DEBUG"), msg)
    }
  }

  def info(msg: => String): Unit = {
    if (level >= Logging.INFO) {
      Gdx.app.log(prefix("INFO"), msg)
    }
  }

  def error(msg: => String, source: Option[Throwable] = None): Unit = {
    if (level >= Logging.ERROR) {
      source match {
        case Some(exc) => Gdx.app.error(prefix("ERROR"), msg, exc)
        case None => Gdx.app.error(prefix("ERROR"), msg)
      }
    }
  }

  def globalLevel: Int = {
    Gdx.app.getLogLevel
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
    val df = new java.text.SimpleDateFormat("yyyy:MM:dd-hh:mm:ss:SSS")
    df.format(new java.util.Date())
  }
}

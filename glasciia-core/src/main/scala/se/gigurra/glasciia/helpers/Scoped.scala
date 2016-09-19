package se.gigurra.glasciia.helpers

/**
  * Created by johan on 2016-09-19.
  */
object Scoped {
  def apply(start: => Unit, content: => Unit, end: => Unit): Unit = {
    start
    try {
      content
    } finally {
      end
    }
  }
}



package com.github.gigurra.glasciia

/**
  * Created by johan on 2016-10-31.
  */
abstract class TimedScene(length: Long) extends Scene {

  def timeLeft: Long = math.max(0, length - elapsed)
  def finished: Boolean = timeLeft == 0
}

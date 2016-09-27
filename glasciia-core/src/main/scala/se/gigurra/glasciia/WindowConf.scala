package se.gigurra.glasciia

import se.gigurra.math.Vec2

/**
  * Created by johan on 2016-09-19.
  */
case class WindowConf(position: Vec2[Int],
                      size: Vec2[Int],
                      resizable: Boolean,
                      maximized: Boolean,
                      title: String)

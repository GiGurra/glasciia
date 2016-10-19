package com.github.gigurra.glasciia.test1

import com.github.gigurra.glasciia.App.{GlConf, WindowConf}
import com.github.gigurra.math.Vec2

/**
  * Created by johan on 2016-10-08.
  */
object Conf {

  val initialWindow = WindowConf(
    position = Vec2(100, 100),
    size = Vec2(640, 480),
    resizable = true,
    maximized = false,
    fullscreen = false,
    title = "Test Window"
  )

  val initialGl = GlConf(
    vsync = true,
    msaa = 4,
    foregroundFpsCap = None,
    backgroundFpsCap = Some(30)
  )
}

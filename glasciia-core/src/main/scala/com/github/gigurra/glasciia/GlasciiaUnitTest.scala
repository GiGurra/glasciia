package com.github.gigurra.glasciia

import com.badlogic.gdx.utils.GdxNativesLoader

/**
  * Created by johan on 2016-12-10.
  */
trait GlasciiaUnitTest {

  /**
    * Created by johan on 2016-12-10.
    */
  trait GdxTest {
    GdxNativesLoader.load()
    Logging.overrideBackend(Logging.TestLoggingBackend)
  }
}

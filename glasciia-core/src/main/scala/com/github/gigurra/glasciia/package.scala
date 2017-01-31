package com.github.gigurra

/**
  * Created by johan on 2017-01-31.
  */
package object glasciia {
  type Binding[T] = Signal[T => Unit]
}

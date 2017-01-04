package com.github.gigurra.glasciia

import scala.concurrent.ExecutionContext

/**
  * Created by johan on 2017-01-04.
  */
object SameThreadExecutionContext extends ExecutionContext {
  override def execute(runnable: Runnable): Unit = runnable.run()
  override def reportFailure(cause: Throwable): Unit = throw cause
}

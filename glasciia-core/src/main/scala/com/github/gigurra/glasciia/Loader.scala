package com.github.gigurra.glasciia

import scala.reflect.ClassTag

/**
  * Created by johan on 2016-10-08.
  */
abstract class Loader[T <: AnyRef : ClassTag] {
  def get(name: String, flush: Boolean = false): Option[T]
  def apply(name: String, flush: Boolean = false): T = get(name, flush).getOrElse(throw new NoSuchElementException(s"Could not find any ${implicitly[ClassTag[T]].runtimeClass} by name '$name' in $this"))
  def flush(force: Boolean = false): Unit
  def dispose(): Unit
}

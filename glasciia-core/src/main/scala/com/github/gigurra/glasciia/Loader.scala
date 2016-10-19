package com.github.gigurra.glasciia

import scala.reflect.ClassTag

/**
  * Created by johan on 2016-10-08.
  */
abstract class Loader[T <: AnyRef : ClassTag] {
  def get(name: String, upload: Boolean = false): Option[T]
  def apply(name: String, upload: Boolean = false): T = get(name, upload).getOrElse(throw new NoSuchElementException(s"Could not find any ${implicitly[ClassTag[T]].runtimeClass} by name '$name' in $this"))
  def uploadIfDirty(): Unit
}

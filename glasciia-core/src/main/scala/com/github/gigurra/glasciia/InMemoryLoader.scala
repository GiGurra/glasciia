package com.github.gigurra.glasciia

import scala.collection.mutable
import scala.reflect.ClassTag

/**
  * Created by johan_home on 2016-10-15.
  */
case class InMemoryLoader[T <: AnyRef : ClassTag](loader: Loader[T],
                                                  explicitlyAdded: mutable.HashMap[String, T] = new mutable.HashMap[String, T]) extends Loader[T] {

  override def get(name: String, upload: Boolean = false): Option[T] = {
    explicitlyAdded.get(name) match {
      case r@Some(_) => r
      case None => loader.get(name, upload) match {
        case r@Some(_) => r
        case None => None
      }
    }
  }

  def add(name: String, value: T): Unit = {
    explicitlyAdded.put(name, value)
  }

  def remove(name: String): Option[T] = {
    explicitlyAdded.remove(name)
  }

  override def uploadIfDirty(): Unit = loader.uploadIfDirty()

  def dispose(): Unit = {
    loader.dispose()
  }
}
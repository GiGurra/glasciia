package com.github.gigurra.glasciia

import scala.collection.mutable
import scala.reflect.ClassTag

/**
  * Created by johan_home on 2016-10-15.
  */
case class InMemoryLoader[T <: AnyRef : ClassTag](impl: Loader[T],
                                                  explicitlyAdded: mutable.HashMap[String, T] = new mutable.HashMap[String, T]) extends Loader[T] {

  override def get(name: String, flush: Boolean = false): Option[T] = {
    explicitlyAdded.get(name) match {
      case r@Some(_) => r
      case None => impl.get(name, flush) match {
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

  override def flush(force: Boolean): Unit = {
    impl.flush(force = force)
  }

  def dispose(): Unit = {
    impl.dispose()
  }
}
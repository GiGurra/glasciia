package se.gigurra.glasciia.impl

import se.gigurra.glasciia.App
import se.gigurra.glasciia.impl.ResourceManager.Resource

import scala.reflect.ClassTag

/**
  * Created by johan on 2016-10-01.
  */
trait ResourceManager { self: App =>

  def addResource[T](path: String, ctor: => T, closer: T => Unit = (x: T) => ()): Unit = executeOnRenderThread {
    val resource = ctor
    resources.put(path, Resource(resource, () => closer(resource))).foreach(_.close())
  }

  def getResource[T: ClassTag](path: String): Option[T] = doGetResource(path).map {
    case resource: T => resource
    case resource => throw new ClassCastException(s"Resource of incorrect type (exp: ${implicitly[ClassTag[T]].runtimeClass}, actual: ${resource.getClass}")
  }

  def resource[T : ClassTag](path: String): T = {
    getResource[T](path) match {
      case Some(resource) => resource
      case None => throw new NoSuchElementException(s"No resource stored on path '$path'")
    }
  }


  /////////////////////////////////////////////
  // Expectations

  protected def executeOnRenderThread(f: => Unit): Unit


  /////////////////////////////////////////////
  // Private

  private def doGetResource(path: String): Option[Any] = {
    resources.get(path).map(_.resource)
  }

  private val resources = new scala.collection.concurrent.TrieMap[String, Resource]
}

object ResourceManager {
  case class Resource(resource: Any, closer: () => Unit) {
    def close() = closer()
  }
}

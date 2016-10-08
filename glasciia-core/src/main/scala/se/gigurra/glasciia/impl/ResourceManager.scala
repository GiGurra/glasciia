package se.gigurra.glasciia.impl

import se.gigurra.glasciia.App
import se.gigurra.glasciia.impl.ResourceManager.{ExplicitTypeRequired, Resource}

import scala.annotation.implicitNotFound
import scala.reflect.Manifest

/**
  * Created by johan on 2016-10-01.
  */
trait ResourceManager { self: App =>

  def addResource[T : ExplicitTypeRequired](path: String, ctor: => T, closer: T => Unit = (x: T) => ())(implicit a: T =:= T): Unit = executeOnRenderThread {
    val resource = ctor
    resources.put(path, Resource(resource, () => closer(resource))).foreach(_.close())
  }

  def getResource[T: Manifest : ExplicitTypeRequired](path: String): Option[T] = doGetResource(path).map {
    case resource: T => resource
    case resource => throw new ClassCastException(s"Resource of incorrect type (exp: ${implicitly[Manifest[T]]}, actual: ${resource.getClass}")
  }

  def resource[T : Manifest : ExplicitTypeRequired](path: String): T = {
    getResource[T](path) match {
      case Some(resource) => resource
      case None => throw new NoSuchElementException(s"No resource stored on path '$path'")
    }
  }


  /////////////////////////////////////////////
  // Expectations

  def executeOnRenderThread(f: => Unit): Unit


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

  @implicitNotFound("Explicit typing required for accessing resources")
  trait ExplicitTypeRequired[T]

  object ExplicitTypeRequired {
    private val evidence: ExplicitTypeRequired[Any] = new Object with ExplicitTypeRequired[Any]

    implicit def notNothingEv[T](implicit n: T =:= T): ExplicitTypeRequired[T] =
      evidence.asInstanceOf[ExplicitTypeRequired[T]]
  }
}

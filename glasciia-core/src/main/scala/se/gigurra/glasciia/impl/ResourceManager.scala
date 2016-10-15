package se.gigurra.glasciia.impl

import se.gigurra.glasciia.App
import se.gigurra.glasciia.impl.ResourceManager.{ExplicitTypeRequired, Resource}

import scala.annotation.implicitNotFound
import scala.reflect.Manifest
import scala.language.existentials

/**
  * Created by johan on 2016-10-01.
  */
trait ResourceManager { self: App =>

  def addResource[T : Manifest : ExplicitTypeRequired](path: String, ctor: => T, closer: T => Unit = (x: T) => ())(implicit a: T =:= T): Unit = executeOnRenderThread {
    val resource = ctor
    resources.put(path, Resource(path, resource, () => closer(resource), implicitly[Manifest[T]])).foreach(_.close())
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

  def listResources: Seq[Resource] = {
    resources.values.toArray[Resource]
  }


  /////////////////////////////////////////////
  // Expectations

  def executeOnRenderThread(f: => Unit): Unit


  /////////////////////////////////////////////
  // Private

  private def doGetResource(path: String): Option[Any] = {
    resources.get(path).map(_.data)
  }

  private val resources = new scala.collection.concurrent.TrieMap[String, Resource]
}

object ResourceManager {
  case class Resource(path: String, data: Any, closer: () => Unit, manifest: Manifest[_]) {
    def close() = closer()
    def class_ : Class[_] = manifest.runtimeClass
    override def toString: String = {
      def toStringManifest(m: Manifest[_]): String = {
        val simpleName = m.runtimeClass.getSimpleName
        m.typeArguments match {
          case Nil => simpleName
          case typeArgs => s"$simpleName[${m.typeArguments.map(toStringManifest).mkString(", ")}]"
        }
      }
      s"$path: ${toStringManifest(manifest)}"
    }

  }

  @implicitNotFound("Explicit typing required for accessing resources")
  trait ExplicitTypeRequired[T]

  object ExplicitTypeRequired {
    private val evidence: ExplicitTypeRequired[Any] = new Object with ExplicitTypeRequired[Any]

    implicit def notNothingEv[T](implicit n: T =:= T): ExplicitTypeRequired[T] =
      evidence.asInstanceOf[ExplicitTypeRequired[T]]
  }
}

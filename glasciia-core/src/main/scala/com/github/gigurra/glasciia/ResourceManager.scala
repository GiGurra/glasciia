package com.github.gigurra.glasciia

import scala.annotation.implicitNotFound
import scala.language.existentials
import scala.reflect.Manifest
import ResourceManager._

/**
  * Created by johan on 2016-10-01.
  */
class ResourceManager extends Logging {

  def add[T : Manifest : ExplicitTypeRequired](path: AnyRef, ctor: => T, closer: T => Unit = (_: T) => ())(implicit a: T =:= T): Unit = {
    val resource = ctor
    resources.put(path, Resource(path, resource, () => closer(resource), implicitly[Manifest[T]])).foreach(_.close())
  }

  def get[T: Manifest : ExplicitTypeRequired](path: AnyRef): Option[T] = doGetResource(path).map {
    case resource: T => resource
    case resource => throw new ClassCastException(s"Resource of incorrect type (exp: ${implicitly[Manifest[T]]}, actual: ${resource.getClass}")
  }

  def apply[T : Manifest : ExplicitTypeRequired](path: AnyRef): T = {
    apply[T](path, default = throw new NoSuchElementException(s"No resource stored on path '$path'"))
  }

  def apply[T : Manifest : ExplicitTypeRequired](path: AnyRef, default: => T, addDefault: Boolean = true): T = {
    get[T](path) match {
      case Some(resource) => resource
      case None =>
        val newValue = default
        if (addDefault)
          add[T](path, newValue)
        newValue
    }
  }

  def listResources: Vector[Resource] = {
    resources.values.toVector
  }

  private def doGetResource(path: AnyRef): Option[Any] = {
    resources.get(path).map(_.data)
  }

  private val resources = new scala.collection.concurrent.TrieMap[AnyRef, Resource]
}

object ResourceManager {
  case class Resource(path: AnyRef, data: Any, closer: () => Unit, manifest: Manifest[_]) {
    def close(): Unit = closer()
    def class_ : Class[_] = manifest.runtimeClass
    override def toString: String = {
      def toStringManifest(m: Manifest[_]): String = {
        val simpleName = m.runtimeClass.getSimpleName
        m.typeArguments match {
          case Nil => simpleName
          case _ => s"$simpleName[${m.typeArguments.map(toStringManifest).mkString(", ")}]"
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

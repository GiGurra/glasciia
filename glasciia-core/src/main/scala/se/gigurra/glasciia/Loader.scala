package se.gigurra.glasciia

import scala.collection.concurrent.TrieMap
import scala.ref.WeakReference
import scala.reflect.ClassTag

/**
  * Created by johan on 2016-10-08.
  */
abstract class Loader[T <: AnyRef : ClassTag] {
  def get(name: String): Option[T]
  def apply(name: String): T = get(name).getOrElse(throw new NoSuchElementException(s"Could not find any ${implicitly[ClassTag[T]].runtimeClass} by name '$name' in $this"))
}

object Loader {

  case class InMemory[T <: AnyRef : ClassTag](fallback: Option[Loader[T]] = None) extends Loader[T] {
    private val cached = new TrieMap[String, WeakReference[T]]
    private val explicitlyAdded = new TrieMap[String, T]

    override def get(name: String): Option[T] = {
      def getFromCache(): Option[T] = cached.get(name).flatMap(_.get)
      def getFromAdded(): Option[T] = explicitlyAdded.get(name)
      getFromAdded().orElse(getFromCache()) match {
        case r@Some(region) => r
        case None => cached.synchronized {
          getFromCache() match {
            case r@Some(region) => r
            case None => fallback.flatMap(_.get(name)) match {
              case r@Some(region) =>
                cached.put(name, WeakReference(region))
                r
              case None => None
            }
          }
        }
      }
    }

    def add(name: String, value: T): Unit = {
      explicitlyAdded.put(name, value)
    }

    def remove(name: String): Option[T] = {
      explicitlyAdded.remove(name)
    }
  }

}
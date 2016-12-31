package com.github.gigurra.glasciia

import java.net.JarURLConnection

import com.badlogic.gdx.files.FileHandle
import com.github.gigurra.lang.FixErasure
import com.github.gigurra.glasciia.Glasciia._

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * Created by johan on 2016-12-31.
  */
abstract class Resources extends ResourceManager {

  private var _loadSomeFinished: Boolean = false
  private val loadTasks: mutable.Queue[Runnable] = new mutable.Queue[Runnable]

  /**
    * @return true if done
    */
  protected def loadSome(): Boolean = true
  protected def addLoadTask(task: Runnable): Unit = {
    loadTasks.enqueue(task)
  }
  protected def addLoadTask(f: => Unit): Unit = {
    addLoadTask(new Runnable {
      override def run(): Unit = f
    })
  }

  final def load(maxTimeMillis: Long): Boolean ={
    val startTime = System.currentTimeMillis()
    def elapsed: Long = System.currentTimeMillis() - startTime

    if (!finished) {
      do {
        if (loadTasks.nonEmpty) {
          loadTasks.dequeue().run()
        } else {
          _loadSomeFinished = loadSome()
        }
      } while(!finished && elapsed < maxTimeMillis)
    }

    _loadSomeFinished
  }

  final def finished: Boolean = {
    loadTasks.isEmpty && _loadSomeFinished
  }
}


object Resources {

  def empty(): Resources = {
    new Resources {
      /**
        * @return true if done
        */
      override protected def loadSome(): Boolean = true
    }
  }

  /*
  val resourceFolders = Vector(
    "animations",
    "backgrounds",
    "cursors",
    "images",
    "particle-effects",
    "pt-mono",
    "shaders"
  )*/

  case class ResourceWalker(impl: Vector[FileHandle] => Vector[FileHandle]) {
    def walk[_: FixErasure](folders: Vector[FileHandle]): Vector[FileHandle] = impl.apply(folders)
    def walk(folders: Vector[String]): Vector[FileHandle] = walk(folders.map(f => f : FileHandle))
  }

  def resourceWalker(lookInsideJar: Boolean): ResourceWalker = ResourceWalker { folders =>
    if (lookInsideJar)
      Resources.walkFileHandles(folders) ++ Resources.walkJarEntries(folders)
    else
      Resources.walkFileHandles(folders)
  }

  def listAll(lookInsideJar: Boolean, resourceFolders: Vector[String]): Vector[FileHandle] = {
    resourceWalker(lookInsideJar).walk(resourceFolders)
  }

  /**
    * Works when running an executable jar file
    */
  def walkJarEntries(folders: Vector[FileHandle]): Vector[FileHandle] = {
    import scala.collection.JavaConverters._

    val url = classOf[Resources].getResource(s"${classOf[Resources].getSimpleName}.class")
    if (url != null) {
      val protocol = url.getProtocol
      if (protocol == "jar") {
        val con = url.openConnection().asInstanceOf[JarURLConnection]
        val archive = con.getJarFile
        val validPrefixes: Set[String] = folders.map(_.path).toSet

        archive.entries.asScala
          .filterNot(_.getName.endsWith(".class"))
          .filterNot(_.getName.endsWith("/"))
          .filter(e => validPrefixes.exists(prefix => e.getName.startsWith(prefix)))
          .map(entry => entry.getName : FileHandle)
          .filterNot(_.isDirectory)
          .toVector
      } else {
        Vector.empty
      }
    } else {
      Vector.empty
    }
  }

  /**
    * Works when running in intellij or on devices, but not in Jar files
    */
  def walkFileHandles(folders: Vector[FileHandle]): Vector[FileHandle] = {
    val out = new ArrayBuffer[FileHandle]
    def fetchChildren(parent: FileHandle): Unit = {
      parent.list.foreach { child =>
        if (child.isDirectory) fetchChildren(child)
        else out += child
      }
    }
    folders.foreach(fetchChildren)
    out.toVector
  }
}

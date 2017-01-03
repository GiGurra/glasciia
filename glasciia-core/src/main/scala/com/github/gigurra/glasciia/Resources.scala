package com.github.gigurra.glasciia

import java.net.JarURLConnection
import java.util.concurrent.Executors

import com.badlogic.gdx.files.FileHandle
import com.github.gigurra.glasciia.Glasciia._
import com.github.gigurra.glasciia.Resources.{AsyncLoadTask, LoadTask, SyncLoadTask}
import com.github.gigurra.lang.FixErasure

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{Future, Promise}
import scala.util.control.NonFatal
import scala.util.{Failure, Success}

/**
  * Created by johan on 2016-12-31.
  */
abstract class Resources extends ResourceManager {

  private var _loadSomeFinished: Boolean = false
  private val loadTasks: mutable.Queue[LoadTask] = new mutable.Queue[LoadTask]
  private lazy val asyncLoader = Executors.newSingleThreadExecutor()

  /**
    * @return true if done
    */
  protected def loadSome(): Boolean = true
  protected def addLoadTask(task: LoadTask): Unit = loadTasks.enqueue(task)
  protected def addLoadTask(f: => Unit): Unit = addLoadTask(SyncLoadTask(() => f))
  protected def addAsyncLoadTask(f: => Unit): Unit = addLoadTask(AsyncLoadTask(() => async(f)))

  private def async(f: => Unit): Future[Unit] = {
    val promise = Promise[Unit]
    asyncLoader.execute(new Runnable {
      override def run(): Unit = {
        try {
          promise.success(f)
        } catch {
          case NonFatal(e) =>
            promise.failure(e)
        }
      }
    })

    promise.future
  }

  final def load(maxTimeMillis: Long): Boolean ={
    val startTime = System.currentTimeMillis()
    def elapsed: Long = System.currentTimeMillis() - startTime

    if (!finished) {
      do {
        if (loadTasks.nonEmpty) {
          if (loadTasks.head.loadSome()) {
            loadTasks.dequeue()
          }
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

  trait LoadTask {
    /**
      * @return
      *         true if finished
      */
    def loadSome(): Boolean
  }

  case class SyncLoadTask(expr: () => Unit) extends LoadTask {
    override def loadSome(): Boolean = {
      expr()
      true
    }
  }

  case class AsyncLoadTask(expr: () => Future[Unit]) extends LoadTask {
    private var futureOpt: Option[Future[Unit]] = None
    override def loadSome(): Boolean = {

      futureOpt match {
        case None =>
          futureOpt = Some(expr())
          false
        case Some(future) => future.value match {
          case None => false
          case Some(Success(_)) => true
          case Some(Failure(e)) => throw e
        }
      }
    }
  }

  def empty(): Resources = {
    new Resources {
      /**
        * @return true if done
        */
      override protected def loadSome(): Boolean = true
    }
  }

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

package com.github.gigurra.glasciia

import java.util.UUID

import com.github.gigurra.glasciia.GameEvent.InputEvent
import com.github.gigurra.glasciia.TransitionSystem.{Cancelled, Transition}

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.control.NonFatal

/**
  * Created by johan on 2017-01-02.
  */
case class TransitionSystem() extends Logging {
  private val entries = new mutable.LinkedHashMap[String, Entry[_ <: Transition]]
  private var _snapshotEntries: Vector[Entry[_ <: Transition]] = Vector.empty
  private var _snapshotTransitions: Vector[Transition] = Vector.empty

  final def snapshot: Vector[Transition] = {
    _snapshotTransitions
  }

  final def update(time: Long, alsoCleanup: Boolean = false): Unit = {
    _snapshotEntries = entries.values.toVector
    _snapshotTransitions = _snapshotEntries.map(_.transition)

    for (entry <- _snapshotEntries) {
      try {
        if (!entry.finished) {
          entry.update(time)
        }
      } catch {
        case NonFatal(e) =>
          entry.cancel(e)
          throw e
      }
    }

    if (alsoCleanup) {
      cleanup()
    }
  }

  final def execute[T <: Transition](transition: T): SameThreadFuture[T] = {
    val promise = SameThreadPromise[T]()
    entries.put(transition.id, Entry[T](transition, promise))
    promise.future
  }

  final def cancel(id: String): Unit = {
    entries.remove(id).foreach(_.cancel())
  }

  final def clear(): Unit = {
    entries.values.foreach(_.cancel())
    entries.clear()
  }

  final def cleanup(): Unit = {
    for (transition <- _snapshotEntries) {
      if (transition.finished) {
        entries.remove(transition.id)
      }
    }
  }

  final def inputHandler: PartialFunction[InputEvent, Unit] = {
    snapshot.map(_.inputHandler).fold(PartialFunction.empty)((acc, item) => acc.orElse(item))
  }

  private case class Entry[T <: Transition](transition: T, promise: SameThreadPromise[T]) {

    def update(time: Long): Unit = {
      if (!finished) {
        transition.act.update(time)
        if (transition.act.finished) {
          promise.success(transition)
        }
      }
    }

    def cancel(cause: Throwable = null): Unit = {
      if (!finished) {
        transition.act.forceFinish()
        promise.failure(Cancelled(cause))
      }
    }

    def id: String = {
      transition.id
    }

    def finished: Boolean = {
      promise.isCompleted
    }
  }
}

object TransitionSystem {

  /**
    * Transitions must execute on the same thread, i.e. the GL thread
    */
  implicit val executionContext = SameThreadExecutionContext

  case class Cancelled(cause: Throwable) extends RuntimeException(cause)

  trait Transition {
    val id: String = UUID.randomUUID.toString
    def act: Act
  }

  object Transition {
    import scala.language.implicitConversions
    implicit def t2Act(t: Transition): Act = t.act
    implicit def t2InputHandler(t: Transition): PartialFunction[InputEvent, Unit] = t.act.inputHandler
  }
}

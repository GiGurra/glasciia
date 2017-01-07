package com.github.gigurra.glasciia

import scala.concurrent.{Future => ScalaFuture, Promise => ScalaPromise}
import scala.language.implicitConversions
import scala.reflect.ClassTag
import scala.util.control.NonFatal
import scala.util.{Failure, Success, Try}

/**
  * Created by johan on 2017-01-07.
  *
  * So to guarantee continuations on the Same/GLThread
  */
case class SameThreadPromise[T](impl: ScalaPromise[T] = ScalaPromise[T]()) {
  final def future: SameThreadFuture[T] = new SameThreadFuture[T](impl.future)
  final def isCompleted: Boolean = impl.isCompleted
  final def complete(result: Try[T]): this.type = { impl.complete(result); this }
  final def tryComplete(result: Try[T]): Boolean = impl.tryComplete(result)
  final def completeWith(other: SameThreadFuture[T]): this.type = { impl.completeWith(other.impl); this }
  final def tryCompleteWith(other: SameThreadFuture[T]): this.type = { impl.tryCompleteWith(other.impl); this }
  final def success(@deprecatedName('v) value: T): this.type = { impl.success(value); this }
  final def trySuccess(value: T): Boolean = impl.trySuccess(value)
  final def failure(@deprecatedName('t) cause: Throwable): this.type = { impl.failure(cause); this }
  final def tryFailure(@deprecatedName('t) cause: Throwable): Boolean = impl.tryFailure(cause)
}

/**
  * Created by johan on 2017-01-07.
  *
  * So to guarantee continuations on the Same/GLThread
  */
case class SameThreadFuture[T](impl: ScalaFuture[T]) {

  private implicit val _executor = SameThreadExecutionContext

  // Just forward everything to scala.concurrent.Future
  final def onSuccess[U](pf: PartialFunction[T, U]): Unit = impl.onSuccess(pf)
  final def onFailure[U](@deprecatedName('callback) pf: PartialFunction[Throwable, U]): Unit = impl.onFailure(pf)
  final def onComplete[U](@deprecatedName('func) f: Try[T] => U): Unit = impl.onComplete(f)
  final def isCompleted: Boolean = impl.isCompleted
  final def value: Option[Try[T]] = impl.value
  final def failed: SameThreadFuture[Throwable] = SameThreadFuture(impl.failed)
  final def foreach[U](f: T => U): Unit = impl.foreach(f)
  final def transform[S](s: T => S, f: Throwable => Throwable): SameThreadFuture[S] = SameThreadFuture(impl.transform(s, f))
  final def map[S](f: T => S): SameThreadFuture[S] = SameThreadFuture(impl.map(f))
  final def filter(@deprecatedName('pred) p: T => Boolean): SameThreadFuture[T] = SameThreadFuture(impl.filter(p))
  final def withFilter(p: T => Boolean): SameThreadFuture[T] = SameThreadFuture(impl.withFilter(p))
  final def collect[S](pf: PartialFunction[T, S]): SameThreadFuture[S] = SameThreadFuture(impl.collect(pf))
  final def recover[U >: T](pf: PartialFunction[Throwable, U]): SameThreadFuture[U] = SameThreadFuture(impl.recover(pf))
  final def mapTo[S : ClassTag]: SameThreadFuture[S] = SameThreadFuture(impl.mapTo[S])
  final def andThen[U](pf: PartialFunction[Try[T], U]): SameThreadFuture[T] = SameThreadFuture(impl.andThen(pf))
  final def flatMap[S](f: T => SameThreadFuture[S]): SameThreadFuture[S] = SameThreadFuture(impl.flatMap(f(_).impl))

  final def recoverWith[U >: T](pf: PartialFunction[Throwable, SameThreadFuture[U]]): SameThreadFuture[U] = {
    // Copy pasta from scala.concurrent.Future
    val p = SameThreadPromise[U]()
    onComplete {
      case Failure(t) => try pf.applyOrElse(t, (_: Throwable) => this).onComplete(p.complete) catch {
        case NonFatal(nft) => p failure nft
      }
      case other => p complete other
    }
    p.future
  }

  final def zip[U](that: SameThreadFuture[U]): SameThreadFuture[(T, U)] = {
    // Copy pasta from scala.concurrent.Future
    val p = SameThreadPromise[(T, U)]()
    onComplete {
      case f: Failure[_] => p complete f.asInstanceOf[Failure[(T, U)]]
      case Success(s) => that onComplete { c => p.complete(c map { s2 => (s, s2) }) }
    }
    p.future
  }

  final def fallbackTo[U >: T](that: SameThreadFuture[U]): SameThreadFuture[U] = {
    // Copy pasta from scala.concurrent.Future
    val p = SameThreadPromise[U]()
    onComplete {
      case s @ Success(_) => p complete s
      case f @ Failure(_) => that onComplete {
        case s2 @ Success(_) => p complete s2
        case _ => p complete f // Use the first failure as the failure
      }
    }
    p.future
  }
}

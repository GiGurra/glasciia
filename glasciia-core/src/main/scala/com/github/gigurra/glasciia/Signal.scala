package com.github.gigurra.glasciia

import scala.collection.mutable
import scala.language.implicitConversions

class Signal[T] {

  protected val slots = new mutable.ArrayBuffer[T => Unit](initialSize = 2)

  def apply(t: T): Unit = {
    slots.foreach(_.apply(t))
  }

  def foreach(slot: T => Unit): this.type = connect(slot)
  def connect(slot: T => Unit): this.type = {
    slots += slot
    this
  }

  def foreach(slot: => Unit): this.type = connect(slot)
  def connect(slot: => Unit): this.type = {
    slots += ((_: T) => slot)
    this
  }

  def map[R](f: T => R): Signal[R] = {
    val out = new Signal[R]
    this.connect(v => out.apply(f(v)))
    out
  }

  def flatMap[R](f: T => Signal[R]): Signal[R] = {
    val out = new Signal[R]
    this.connect { v =>
      f(v).connect(v2 => out.apply(v2))
    }
    out
  }

  def filter(f: T => Boolean): Signal[T] = {
    val out = new Signal[T]
    this.connect(v => if (f(v)) out.apply(v))
    out
  }

  def unit: Signal[Unit] = {
    map(_ => ())
  }
}

object Signal {
  def apply[T]: Signal[T] = new Signal[T]
  implicit def toSignal(s: Signal.type): Signal[Unit] = Signal.apply[Unit]

  implicit class BindingImpl[T](signal: Signal[T => Unit]) {

    def option: Option[T] = {
      var out: Option[T] = None
      signal.apply(v => out = Some(v))
      out
    }

    def get: T = {
      option.getOrElse(throw new NoSuchElementException(s"No data bound to Getter/Signal"))
    }

    def bind(f: => T): Signal[T => Unit] = {
      signal.connect(_(f))
      signal
    }
  }

  implicit def binding2Value[T](signal: Signal[T => Unit]): T = signal.get
}

object Binding {
  def apply[T]: Signal[T => Unit] = new Signal[T => Unit]
}

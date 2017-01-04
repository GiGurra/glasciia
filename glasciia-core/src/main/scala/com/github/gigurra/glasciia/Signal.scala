package com.github.gigurra.glasciia

import scala.collection.mutable
import scala.language.implicitConversions

class Signal[T] {
  protected val slots = new mutable.ArrayBuffer[T => Unit](initialSize = 2)
  def apply(t: T): Unit = slots.foreach(_.apply(t))
  def connect(slot: T => Unit): Unit = slots += slot
  def connect(slot: => Unit): Unit = slots += ((_: T) => slot)
}

class Signal2[T1, T2] extends Signal[(T1, T2)] {
  def apply(t1: T1, t2: T2): Unit = slots.foreach(_.apply(t1, t2))
  def connect(slot: (T1, T2) => Unit): Unit = slots += (tuple => slot(tuple._1, tuple._2))
}

class Signal3[T1, T2, T3] extends Signal[(T1, T2, T3)] {
  def apply(t1: T1, t2: T2, t3: T3): Unit = slots.foreach(_.apply(t1, t2, t3))
  def connect(slot: (T1, T2, T3) => Unit): Unit = slots += (tuple => slot(tuple._1, tuple._2, tuple._3))
}

class Signal4[T1, T2, T3, T4] extends Signal[(T1, T2, T3, T4)] {
  def apply(t1: T1, t2: T2, t3: T3, t4: T4): Unit = slots.foreach(_.apply(t1, t2, t3, t4))
  def connect(slot: (T1, T2, T3, T4) => Unit): Unit = slots += (tuple => slot(tuple._1, tuple._2, tuple._3, tuple._4))
}

class Signal5[T1, T2, T3, T4, T5] extends Signal[(T1, T2, T3, T4, T5)] {
  def apply(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5): Unit = slots.foreach(_.apply(t1, t2, t3, t4, t5))
  def connect(slot: (T1, T2, T3, T4, T5) => Unit): Unit = slots += (tuple => slot(tuple._1, tuple._2, tuple._3, tuple._4, tuple._5))
}

class Signal6[T1, T2, T3, T4, T5, T6] extends Signal[(T1, T2, T3, T4, T5, T6)] {
  def apply(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6): Unit = slots.foreach(_.apply(t1, t2, t3, t4, t5, t6))
  def connect(slot: (T1, T2, T3, T4, T5, T6) => Unit): Unit = slots += (tuple => slot(tuple._1, tuple._2, tuple._3, tuple._4, tuple._5, tuple._6))
}

object Signal {
  def apply[T]: Signal[T] = new Signal[T]
  def apply[T1, T2]: Signal2[T1, T2] = new Signal2[T1, T2]
  def apply[T1, T2, T3]: Signal3[T1, T2, T3] = new Signal3[T1, T2, T3]
  def apply[T1, T2, T3, T4]: Signal4[T1, T2, T3, T4] = new Signal4[T1, T2, T3, T4]
  def apply[T1, T2, T3, T4, T5]: Signal5[T1, T2, T3, T4, T5] = new Signal5[T1, T2, T3, T4, T5]
  def apply[T1, T2, T3, T4, T5, T6]: Signal6[T1, T2, T3, T4, T5, T6] = new Signal6[T1, T2, T3, T4, T5, T6]

  implicit def toSignal(s: Signal.type): Signal[Unit] = Signal.apply[Unit]
}

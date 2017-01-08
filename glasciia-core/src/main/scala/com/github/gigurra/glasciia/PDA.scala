package com.github.gigurra.glasciia

import scala.collection.mutable

/**
  * Created by johan on 2017-01-08.
  */
class PDA[State](stack: mutable.Stack[State]) {

  def states: Vector[State] = stack.toVector
  def reverse: Vector[State] = states.reverse
  def isEmpty: Boolean = stack.isEmpty
  def nonEmpty: Boolean = stack.nonEmpty
  def size: Int = stack.size

  def contains(state: State): Boolean = {
    stack.contains(state)
  }

  def isTopMost(state: State): Boolean = {
    headOption.contains(state)
  }

  def head: State = {
    require(nonEmpty, "called head on empty PDA")
    stack.head
  }

  def headOption: Option[State] = {
    stack.headOption
  }

  def peek: State = {
    require(nonEmpty, "called peek on empty PDA")
    stack.head
  }

  def peekOption: Option[State] = {
    stack.headOption
  }

  def push(state: State): this.type = {
    stack.push(state)
    this
  }

  def push(states: State*): this.type = {
    states.reverse foreach push
    this
  }

  def pop(): State = {
    require(stack.nonEmpty, "Cannot pop empty PDA stack")
    stack.pop()
  }

  def clear(): Vector[State] = {
    val prevStates = states
    stack.clear()
    prevStates
  }
}

object PDA {
  def apply[State](states: State*): PDA[State] = {
    val stack = new mutable.Stack[State]
    states foreach stack.push
    new PDA(stack)
  }
}

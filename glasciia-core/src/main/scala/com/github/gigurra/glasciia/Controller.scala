package com.github.gigurra.glasciia

/**
  * Created by johan on 2016-10-02.
  */
trait Controller {
  def index: Int
  def name: String
  def axes: Vector[Axis]
  def buttons: Vector[Button]
  def metadata: Any
}

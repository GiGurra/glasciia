package se.gigurra.glasciia.impl

/**
  * Created by johan on 2016-10-02.
  */
trait Controller {
  def index: Int
  def name: String
  def axes: Seq[Axis]
  def buttons: Seq[Button]
}

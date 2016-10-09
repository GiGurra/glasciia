package se.gigurra.glasciia

import com.badlogic.gdx.InputAdapter

import scala.collection.mutable

/**
  * Created by johan on 2016-10-09.
  */
case class Keyboard(autoReleaseOnFocusLoss: Boolean = true) extends InputAdapter {

  private var propagatedDown = new mutable.HashSet[Int]

  def isKeyDown(vKey: Int): Boolean = {
    propagatedDown(vKey)
  }

  override def keyDown(keycode: Int): Boolean = {
    propagatedDown += keycode
    false
  }

  override def keyUp(keycode: Int): Boolean = {
    propagatedDown -= keycode
    false
  }

  def clear(): Unit = {
    propagatedDown.clear()
  }

  val releaseHook = new InputAdapter {
    override def keyUp(keycode: Int): Boolean = Keyboard.this.keyUp(keycode)
  }
}

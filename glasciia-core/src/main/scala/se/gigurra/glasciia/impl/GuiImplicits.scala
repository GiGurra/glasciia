package se.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.{Actor, Stage}
import com.badlogic.gdx.scenes.scene2d.ui.{Cell, Label, Table, TextButton, Image => Scene2dImage}
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import scala.collection.JavaConversions._
import scala.language.implicitConversions
import scala.reflect.ClassTag

/**
  * Created by johan on 2016-10-02.
  */
trait GuiImplicits extends InputListeners {

  implicit class TableImplicitsOps(table: Table) {
    def cell[T <: Actor](actor: T): Cell[T] = table.add[T](actor)
    def cell(): Cell[_] = table.add()
    def cellImg(template: String, color: Color): Cell[Scene2dImage] = cell(new Scene2dImage(table.getSkin.newDrawable(template, color)))

    def skin = table.getSkin

    def addStyle[T](name: String, styleClass: Class[T])(style: T): Table = {
      skin.add(name, style, styleClass)
      table
    }

    def addStyle[T](styleClass: Class[T])(style: T): Table = {
      addStyle("default", styleClass)(style)
    }

    def newDrawable(style: String, color: Color): Drawable = {
      skin.newDrawable(style, color)
    }

    def style[T: ClassTag](name: String): T = {
      skin.get(name, implicitly[ClassTag[T]].runtimeClass.asInstanceOf[Class[T]])
    }

    def style[T: ClassTag]: T = {
      style[T]("default")
    }

    def rw[A](f: Table => A): Table = {
      f(table)
      table.row()
      table
    }
  }

  implicit class ActorImplicitsOps(actor: Actor) {
    def show(): Unit = actor.setVisible(true)
    def hide(): Unit = actor.setVisible(false)
    def hidden: Boolean = !actor.isVisible
    def visible: Boolean = !hidden
  }

  implicit class StageImplicitOps(stage: Stage) {
    def actors: Seq[Actor] = stage.getActors.toSeq
    def show(): Unit = actors.foreach(_.show())
    def hide(): Unit = actors.foreach(_.hide())
    def hidden: Boolean = actors.headOption.fold(true)(!_.visible)
    def visible: Boolean = !hidden
  }

  implicit class TextButtonImplicitsOps(button: TextButton) {
    def fontScale(value: Float): TextButton = {
      button.getLabel.setFontScale(value)
      button
    }
  }

  implicit class LabelImplicitsOps(label: Label) {
    def fontScale(value: Float): Label = {
      label.setFontScale(value)
      label
    }
  }
}

object GuiImplicits extends GuiImplicits

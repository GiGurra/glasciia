package se.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.{Cell, Label, Table, TextButton, Image => Scene2dImage}
import com.badlogic.gdx.scenes.scene2d.utils.Drawable

import scala.language.implicitConversions
import scala.reflect.ClassTag

/**
  * Created by johan on 2016-10-02.
  */
trait GuiImplicits {

  implicit class TableOpsImplicits[T_Table <: Table](table: T_Table) {
    def cell[T <: Actor](actor: T): Cell[T] = table.add[T](actor)
    def cell(): Cell[_] = table.add()
    def cellImg(template: String, color: Color): Cell[Scene2dImage] = cell(new Scene2dImage(table.getSkin.newDrawable(template, color)))

    def skin = table.getSkin

    def addStyle[T](name: String, styleClass: Class[T])(style: T): T_Table = {
      skin.add(name, style, styleClass)
      table
    }

    def addStyle[T](styleClass: Class[T])(style: T): T_Table = {
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

    def rw[A](f: Table => A): T_Table = {
      f(table)
      table.row()
      table
    }
  }

  implicit class TextButtonOpsImplicits(button: TextButton) {
    def fontScale(value: Float): TextButton = {
      button.getLabel.setFontScale(value)
      button
    }
  }

  implicit class LabelImplicits(label: Label) {
    def fontScale(value: Float): Label = {
      label.setFontScale(value)
      label
    }
  }
}

object GuiImplicits extends GuiImplicits

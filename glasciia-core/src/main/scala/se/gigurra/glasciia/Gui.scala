package se.gigurra.glasciia

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.Drawable

import scala.language.implicitConversions
import scala.reflect.ClassTag

/**
  * Created by johan on 2016-10-08.
  */
case class Gui[T_Table <: Table](table: T_Table,
                                 fillParent: Boolean = false,
                                 debug: Boolean = false) {

  table.setFillParent(fillParent)
  table.setDebug(debug)

  def skin = table.getSkin

  def addStyle[T](name: String, styleClass: Class[T], style: T): Gui[T_Table] = {
    skin.add(name, style, styleClass)
    this
  }

  def newDrawableFromStyle(name: String, color: Color): Drawable = {
    skin.newDrawable(name, color)
  }

  def getStyle[T: ClassTag](name: String): T = {
    skin.get(name, implicitly[ClassTag[T]].runtimeClass.asInstanceOf[Class[T]])
  }

  def row[A](f: Table => A): Gui[T_Table] = {
    f(table)
    table.row()
    this
  }
}

object Gui {
  implicit def gui2table[T <: Table](gui: Gui[T]): Table = gui.table.asInstanceOf[Table]
}

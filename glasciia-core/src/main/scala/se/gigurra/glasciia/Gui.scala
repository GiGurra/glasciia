package se.gigurra.glasciia

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.{Skin, Table}
import com.badlogic.gdx.scenes.scene2d.utils.Drawable

import scala.language.implicitConversions
import scala.reflect.ClassTag

/**
  * Created by johan on 2016-10-08.
  */
case class Gui(skin: Skin = new Skin(),
               stage: Stage = new Stage(),
               var hidden: Boolean = false,
               debug: Boolean = false) {

  val table = new Table(skin)
  table.setFillParent(true)
  stage.addActor(table)
  table.setDebug(debug)

  def addStyle[T](name: String, styleClass: Class[T], style: T): Gui = {
    skin.add(name, style, styleClass)
    this
  }

  def newDrawableFromStyle(name: String, color: Color): Drawable = {
    skin.newDrawable(name, color)
  }

  def getStyle[T: ClassTag](name: String): T = {
    skin.get(name, implicitly[ClassTag[T]].runtimeClass.asInstanceOf[Class[T]])
  }

  def row[A](f: Table => A): Gui = {
    f(table)
    table.row()
    this
  }

  def draw(canvas: Canvas, dt: Float = 0.0f): Gui = {
    if (!hidden) {
      stage.getViewport.setScreenSize(canvas.width, canvas.height)
      stage.getViewport.setWorldSize(canvas.width, canvas.height)
      stage.getViewport.update(canvas.width, canvas.height, true)
      table.layout()
      if (dt != 0.0f)
        stage.act(dt)
      stage.draw()
    }
    this
  }
  def hide(): Unit = hidden = true
  def show(): Unit = hidden = false
}

object Gui {
  implicit def gui2stage(gui: Gui): Stage = gui.stage
  implicit def gui2table(gui: Gui): Table = gui.table
}

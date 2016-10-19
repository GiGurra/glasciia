package com.github.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.{Cell, Label, Skin, Table, TextButton, Image => Scene2dImage}
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.{Actor, Stage}

import scala.collection.JavaConversions._
import scala.language.implicitConversions
import scala.reflect.ClassTag

/**
  * Created by johan on 2016-10-02.
  */
trait GuiImplicits extends ActorImplicits {

  class skinOps[SelfType](self: SelfType, val skin: Skin) {

    def addStyle[T : ClassTag](style: T): SelfType = addStyle[T]("default", style)
    def addStyle[T : ClassTag](name: String, style: T): SelfType = {
      skin.add(name, style, implicitly[ClassTag[T]].runtimeClass)
      self
    }

    def newInstance(style: String, color: Color): Drawable = skin.newDrawable(style, color)

    def style[T: ClassTag](name: String): T = skin.get(name, implicitly[ClassTag[T]].runtimeClass.asInstanceOf[Class[T]])
    def style[T: ClassTag]: T = style[T]("default")
  }

  implicit class SkinImplicitOps(skin: Skin) extends skinOps[Skin](skin, skin)

  implicit class TableImplicitsOps(table: Table) extends skinOps[Table](table, table.getSkin) {
    def cell[T <: Actor](actor: T): Cell[T] = table.add[T](actor)
    def cell(): Cell[_] = table.add()
    def cellImg(template: String, color: Color): Cell[Scene2dImage] = cell(new Scene2dImage(table.getSkin.newDrawable(template, color)))

    def rw[A](f: Table => A): Table = {
      f(table)
      table.row()
      table
    }

    def stage: Stage = table.getStage

    def debug(value: Boolean): Table = {
      table.setDebug(value)
      table
    }

    def fillParent(value: Boolean): Table = {
      table.setFillParent(value)
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

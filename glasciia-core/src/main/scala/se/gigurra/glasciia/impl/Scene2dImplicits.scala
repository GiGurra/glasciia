package se.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.{Cell, Table}
import com.badlogic.gdx.scenes.scene2d.ui.{Image => Scene2dImage}

import scala.language.implicitConversions

/**
  * Created by johan on 2016-10-02.
  */
trait Scene2dImplicits {

  implicit class TableOpsImplicits(val table: Table) {
    def cell[T <: Actor](actor: T): Cell[T] = table.add[T](actor)
    def cell(): Cell[_] = table.add()
    def cell(template: String, color: Color): Cell[Scene2dImage] = cell(new Scene2dImage(table.getSkin.newDrawable(template, color)))
  }
}

object Scene2dImplicits extends Scene2dImplicits

package se.gigurra.glasciia

import com.badlogic.gdx.scenes.scene2d.ui.Table
import se.gigurra.glasciia.impl.GuiImplicits

import scala.language.implicitConversions

/**
  * Created by johan on 2016-10-08.
  */
case class Gui[T_Table <: Table](table: T_Table,
                                 fillParent: Boolean = false,
                                 debug: Boolean = false) {

  table.setFillParent(fillParent)
  table.setDebug(debug)
}

object Gui extends GuiImplicits {
  implicit def gui2table[T <: Table](gui: Gui[T]): Table = gui.table.asInstanceOf[Table]
  implicit def gui2RichTable[T <: Table](gui: Gui[T]): TableOpsImplicits[T] = new TableOpsImplicits(gui.table)
}

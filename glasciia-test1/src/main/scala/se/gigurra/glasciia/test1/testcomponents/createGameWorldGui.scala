package se.gigurra.glasciia.test1.testcomponents

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.{Table, TextButton}
import se.gigurra.glasciia._
import se.gigurra.glasciia.Glasciia._

/**
  * Created by johan on 2016-10-09.
  */
object createGameWorldGui {

  def apply(app: App, regions: Loader.InMemory[TextureRegion]): Stage = {
    val root: Gui = RootGui(debug = true)
    val stage = root.stage
    val table = root.table
    val skin = root.skin

    addDefaultGuiStyles(app, skin, regions)


    //////////////////////////////////////////////////
    // First we separate the screen into five blocks

    //////////////////////////////////////////////////
    // 1.                                           //
    //////////////////////////////////////////////////
    // 2.                                           //
    //                                              //
    //                                              //
    //                                              //
    //                                              //
    //                                              //
    //                                              //
    //                                              //
    //////////////                      //////////////
    // 3.       ////////////////////////// 5.       //
    //          // 4.                   //          //
    //          //                      //          //
    //////////////////////////////////////////////////

    // 1. status bar. 0-7% of screen height
    // 2. game draw area (in general, we shouldn't put any UI components here) 7%-75% of screen height
    // 3. mini map. 75%-100% of screen height
    // 4. unit bar. 80%-100% of screen height
    // 5. Command area. 75%-100% of screen height

    val statusBar = Gui(new Table(skin), fillParent = false)
    val gameArea = Gui(new Table(skin), fillParent = false)
    //val miniMap = Gui(new Table(skin), fillParent = true)
    val unitBar = Gui(new Table(skin), fillParent = false)
    val commandArea = Gui(new Table(skin), fillParent = false)
    val bottom = Gui(new Table(skin), fillParent = false, debug = true)

    val button = new TextButton("minimap", skin)
   // miniMap.setSize(640, 100)
  //  miniMap.setFillParent(true)
    val miniMap = skin.newInstance("fill", Color.YELLOW)

    bottom.table.rw { r =>
      r.cellImg("fill", Color.YELLOW).prefWidth(640.0f * 0.33f).growY()
      r.cellImg("fill",  Color.GREEN).prefWidth(640.0f * 0.6f).prefHeight(0.20f * 480).growX().bottom()
      r.cellImg("fill", Color.YELLOW).prefWidth(640.0f * 0.33f).growY()
      r.bottom()
    }

    root.rw { r => r.cell(statusBar.table).growX().height(640.0f * 0.07f) }
    root.rw { r => r.cell(gameArea.table).grow() }
    root.rw { r => r.cell(bottom.table).growX().height(480.0f * 0.33f).bottom() }

    stage
  }
}

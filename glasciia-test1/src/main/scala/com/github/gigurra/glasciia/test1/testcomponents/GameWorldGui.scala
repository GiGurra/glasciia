package com.github.gigurra.glasciia.test1.testcomponents

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.{Table, TextButton}
import com.github.gigurra.glasciia._
import com.github.gigurra.glasciia.Glasciia._

/**
  * Created by johan on 2016-10-09.
  */
class GameWorldGui(resources: ResourceManager,
                   regions: InMemoryLoader[TextureRegion]) extends RootGui {

  /* constructor */ {
    val skin = rootTable.debug(true).skin

    addDefaultGuiStyles(resources, skin, regions)

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

    // 1. status bar. 0->10% of screen height
    // 2. game draw area (in general, we shouldn't put any UI components here) 7%->75% of screen height
    // 3. mini map. 75%->100% of screen height
    // 4. unit bar. 80%->100% of screen height
    // 5. Command area. 75%->100% of screen height

    val statusBar = new TextButton("status-bar", skin)
    val gameArea = new Table(skin)
    val miniMap = new TextButton("mini-map", skin)
    val unitBar = new TextButton("unit-bar", skin)
    val commandArea = new TextButton("cmd-area", skin)

    rootTable.rw {
      _.cell(statusBar).colspan(3).growX().prefHeight(640.0f * 0.10f)
    }

    rootTable.rw {
      _.cell(gameArea).colspan(3).grow()
    }

    rootTable.rw { r =>
      r.cell(miniMap).prefWidth(640.0f * 0.33f).prefHeight(480.0f * 0.33f).fill()
      r.cell(unitBar).prefHeight(0.20f * 480).expandX().bottom().fillX()
      r.cell(commandArea).prefWidth(640.0f * 0.33f).prefHeight(480.0f * 0.33f).fill()
      r.bottom()
    }

  }
}

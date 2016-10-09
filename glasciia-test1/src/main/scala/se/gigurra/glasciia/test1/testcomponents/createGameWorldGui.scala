package se.gigurra.glasciia.test1.testcomponents

import com.badlogic.gdx.graphics.g2d.{Batch, TextureRegion}
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.{Table, TextButton}
import se.gigurra.glasciia._
import se.gigurra.glasciia.Glasciia._

/**
  * Created by johan on 2016-10-09.
  */
object createGameWorldGui {

  def apply(app: App, batch: Batch, regions: Loader.InMemory[TextureRegion]): Stage = {
    val (stage, root) = RootGui(batch)
    val skin = root.debug(true).skin

    addDefaultGuiStyles(app, skin, regions)

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

    root.rw {
      _.cell(statusBar).colspan(3).growX().prefHeight(640.0f * 0.10f)
    }
    root.rw {
      _.cell(gameArea).colspan(3).grow()
    }
    root.rw { r =>
      r.cell(miniMap).prefWidth(640.0f * 0.33f).prefHeight(480.0f * 0.33f).fill()
      r.cell(unitBar).prefHeight(0.20f * 480).expandX().bottom().fillX()
      r.cell(commandArea).prefWidth(640.0f * 0.33f).prefHeight(480.0f * 0.33f).fill()
      r.bottom()
    }

    stage
  }
}

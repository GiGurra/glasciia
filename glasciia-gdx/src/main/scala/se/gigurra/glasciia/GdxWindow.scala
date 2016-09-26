package se.gigurra.glasciia

/**
  * Created by johan on 2016-09-21.
  */
class GdxWindow(initialWindowConf: WindowConf,
                initialCameraConf: CameraConf,
                val foregroundFpsCap: Option[Int] = None,
                val backgroundFpsCap: Option[Int] = Some(30),
                val vsync: Boolean = true,
                val msaa: Int = 4)
  extends Window(initialWindowConf, initialCameraConf)
  with LwjglImplementation {

  override def draw(c: Char,
                    size: Float,
                    bold: Boolean,
                    italic: Boolean,
                    foreground: Color,
                    background: Color): Unit = {
    ???
  }

}

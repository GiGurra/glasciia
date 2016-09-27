package se.gigurra.glasciia

/**
  * Created by johan on 2016-09-27.
  */
case class GlConf(vsync: Boolean = true,
                  msaa: Int = 4,
                  foregroundFpsCap: Option[Int] = None,
                  backgroundFpsCap: Option[Int] = Some(30)) {

}

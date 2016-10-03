package se.gigurra.glasciia

import com.badlogic.gdx.Gdx
import se.gigurra.glasciia.impl._
import se.gigurra.math.Vec2

/**
  * Created by johan on 2016-09-28.
  */
object Glasciia
  extends GLCStyle
    with ColorImplicits
    with se.gigurra.math.VecImplicits
    with VecImplicits
    with EventFilters
    with File2String
    with ParticleSourceImplicits
    with ImageResizing {

}

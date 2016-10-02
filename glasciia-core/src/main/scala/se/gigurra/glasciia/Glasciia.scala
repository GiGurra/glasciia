package se.gigurra.glasciia

import se.gigurra.glasciia.impl._

/**
  * Created by johan on 2016-09-28.
  */
trait Glasciia
  extends GLCStyle
    with ColorImplicits
    with se.gigurra.math.VecImplicits
    with VecImplicits
    with EventFilters
    with File2String
    with ParticleEffectImplicits

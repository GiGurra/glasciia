package se.gigurra.glasciia

import se.gigurra.glasciia.impl._

/**
  * Created by johan on 2016-09-28.
  */
object Glasciia
  extends GLCStyle
    with ColorImplicits
    with VecImplicits
    with EventFilters
    with FileReadImplicits
    with ParticleSourceImplicits
    with ImageResizing
    with TextureAtlasCreation
    with CursorCreation {
}

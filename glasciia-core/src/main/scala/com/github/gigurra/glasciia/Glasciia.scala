package com.github.gigurra.glasciia

import com.github.gigurra.glasciia.impl._

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
    with CursorCreation
    with GuiImplicits
    with ImageImplicits
    with FontImplicits
    with InputImplicits {
}

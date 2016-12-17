package com.github.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.Pixmap
import com.github.gigurra.math.Vec2

/**
  * Created by johan on 2016-10-03.
  */
trait ImageResizing {
  def resizePixmap(source: Pixmap, size: Vec2, deleteSource: Boolean, newFormat: Option[Pixmap.Format] = None): Pixmap = {
    val pm2 = new Pixmap(size.x.toInt, size.y.toInt, newFormat.getOrElse(source.getFormat))
    // src: Pixmap,        srcx, srcy,        srcWidth,       srcHeight,  dstx, dsty, dstWidth, dstHeight
    pm2.drawPixmap(source,    0,    0, source.getWidth, source.getHeight,    0,    0,   size.x.toInt,    size.y.toInt)
    if (deleteSource)
      source.dispose()
    pm2
  }
}

object ImageResizing extends ImageResizing

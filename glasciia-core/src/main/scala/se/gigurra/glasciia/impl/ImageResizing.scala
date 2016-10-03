package se.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.Pixmap
import se.gigurra.math.Vec2

/**
  * Created by johan on 2016-10-03.
  */
trait ImageResizing {
  def resize(source: Pixmap, size: Vec2[Int], deleteSource: Boolean, newFormat: Option[Pixmap.Format] = None): Pixmap = {
    println(source.getFormat)
    val pm2 = new Pixmap(size.x, size.y, newFormat.getOrElse(source.getFormat))
    // src: Pixmap,        srcx, srcy,        srcWidth,       srcHeight,  dstx, dsty, dstWidth, dstHeight
    pm2.drawPixmap(source,    0,    0, source.getWidth, source.getHeight,    0,    0,   size.x,    size.y)
    if (deleteSource)
      source.dispose()
    pm2
  }
}

object ImageResizing extends ImageResizing

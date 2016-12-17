package com.github.gigurra.glasciia

import com.badlogic.gdx.graphics.g2d.{Batch, TextureRegion}
import com.badlogic.gdx.scenes.scene2d.utils.{BaseDrawable, Drawable => GdxScene2dDrawable}
import com.github.gigurra.math.Vec2

/**
  * Created by johan on 2016-10-07.
  */
class ChangingRegionDrawable(fRegion: => TextureRegion) extends GdxScene2dDrawable {

  private lazy val baseDrawable: GdxScene2dDrawable = {
    val out = new BaseDrawable()
    out.setMinWidth(width)
    out.setMinHeight(height)
    out
  }

  def region: TextureRegion = fRegion

  def width: Float = region.getRegionWidth
  def height: Float = region.getRegionHeight
  def size: Vec2 = Vec2(region.getRegionWidth, region.getRegionHeight)

  def u: Float = region.getU
  def u2: Float = region.getU2
  def v: Float = region.getV
  def v2: Float = region.getV2
  def uuSize: Float = u2 - u
  def vvSize: Float = v2 - v

  override def setTopHeight(topHeight: Float): Unit = baseDrawable.setTopHeight(topHeight)
  override def getMinHeight: Float = baseDrawable.getMinHeight
  override def setLeftWidth(leftWidth: Float): Unit = baseDrawable.setLeftWidth(leftWidth)
  override def getRightWidth: Float = baseDrawable.getRightWidth
  override def draw(batch: Batch, x: Float, y: Float, width: Float, height: Float): Unit = batch.draw(region, x, y, width, height)
  override def getMinWidth: Float = baseDrawable.getMinWidth
  override def setMinWidth(minWidth: Float): Unit = baseDrawable.setMinWidth(minWidth)
  override def setMinHeight(minHeight: Float): Unit = baseDrawable.setMinHeight(minHeight)
  override def setRightWidth(rightWidth: Float): Unit = baseDrawable.setRightWidth(rightWidth)
  override def setBottomHeight(bottomHeight: Float): Unit = baseDrawable.setBottomHeight(bottomHeight)
  override def getTopHeight: Float = baseDrawable.getTopHeight
  override def getLeftWidth: Float = baseDrawable.getLeftWidth
  override def getBottomHeight: Float = baseDrawable.getBottomHeight
}

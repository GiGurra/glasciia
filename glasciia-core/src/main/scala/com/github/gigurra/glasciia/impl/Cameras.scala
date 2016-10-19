package com.github.gigurra.glasciia.impl

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector3
import com.github.gigurra.math.{Box2, Vec2}
import com.github.gigurra.glasciia.Glasciia._

/**
  * Created by johan on 2016-10-01.
  */
trait Cameras {

  val camera = new OrthographicCamera

  def zoom: Float = camera.zoom
  def cameraPos: Vec2[Float] = Vec2(camera.position.x, camera.position.y)
  def cameraSize: Vec2[Float] = Vec2(camera.viewportWidth, camera.viewportHeight) * math.abs(camera.zoom)
  def cameraBounds: Box2[Float] = Box2(ll = cameraPos - cameraSize / 2.0f, size = cameraSize)

  def setCameraPos(pos: Vec2[Float]): Unit = camera.position.set(pos)
  def mousePos: Vec2[Int]

  def setZoom(newValue: Float, preserveMouseWorldPosition: Boolean, projectionArea: Box2[Float]): Unit = {
    val mouseWorldPosBefore = screen2World(mousePos, projectionArea)
    camera.zoom = newValue
    camera.update()
    val mouseWorldPosAfter = screen2World(mousePos, projectionArea)
    val correction = mouseWorldPosBefore - mouseWorldPosAfter
    setCameraPos(cameraPos + correction)
  }

  def setOrtho(yDown: Boolean, width: Float, height: Float): Unit = {
    camera.setToOrtho(yDown, width, height)
  }

  def setOrtho(yDown: Boolean, width: Float, height: Float, scaling: Float): Unit = {
    camera.setToOrtho(yDown, width / scaling, height / scaling)
  }

  def screen2World(screenPos: Vec2[Int], projectionArea: Box2[Float]): Vec2[Float] = {
    val out = new Vector3(screenPos.x, screenPos.y, 0.0f)
    camera.unproject(out, projectionArea.ll.x, projectionArea.ll.y, projectionArea.width, projectionArea.height)
    Vec2[Float](out.x, out.y)
  }

  def world2Screen(screenPos: Vec2[Float], projectionArea: Box2[Float]): Vec2[Int] = {
    val out = new Vector3(screenPos.x, screenPos.y, 0.0f)
    camera.project(out, projectionArea.ll.x, projectionArea.ll.y, projectionArea.width, projectionArea.height)
    Vec2[Int](out.x.toInt, out.y.toInt)
  }
}

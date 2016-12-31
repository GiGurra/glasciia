package com.github.gigurra.glasciia

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.github.gigurra.math.Vec2

/**
  * Created by johan on 2016-12-30.
  */
case class PreparedRepeatedImage(segments: Array[Float],
                                 segmentImage: TextureRegion,
                                 transform: Transform,
                                 connections: Array[Float],
                                 connectionImage: Option[TextureRegion])

object PrepareLinePolygon {

  def apply(points: Vector[Vec2],
            width: Float,
            lineImage: TextureRegion,
            cornerImage: Option[TextureRegion] = None,
            transform: Transform = Transform.IDENTITY,
            closed: Boolean = true): PreparedRepeatedImage = {

    require(points.length >= 2, "Need at least two points to draw lines")

    val n = points.length
    val startIndices: Vector[Int] =
      if (closed) points.indices.toVector
      else points.indices.toVector.dropRight(1)

    val vertices = new Array[Float](startIndices.length * 8)
    val nCorners = if (closed) startIndices.length else startIndices.length - 1
    val corners = new Array[Float](if (cornerImage.nonEmpty) nCorners * 8 else 0)
    var isFirst = true

    var segmentVertexOffset = 0
    var cornerVertexOffset = 0

    for {
      iCurrent <- startIndices
    } {

      val iNext = (iCurrent + 1) % n
      val iPrev = (n + iCurrent - 1) % n
      val current = points(iCurrent)
      val next = points(iNext)
      val prev = points(iPrev)
      val prevLength = distance(current, prev)
      val nextLength = distance(current, next)
      val nextRight = Vec2((next.x - current.x) / nextLength, (next.y - current.y) / nextLength)
      val prevRight = Vec2((current.x - prev.x) / nextLength, (current.y - prev.y) / prevLength)
      val nextUp = nextRight.orthogonal
      val prevUp = prevRight.orthogonal

      { // Write

        val radiusNext = nextUp * width * 0.5f
        val ll = current - radiusNext
        val ul = current + radiusNext
        val ur = next + radiusNext
        val lr = next - radiusNext

        vertices(segmentVertexOffset + 0) = ll.x
        vertices(segmentVertexOffset + 1) = ll.y
        vertices(segmentVertexOffset + 2) = ul.x
        vertices(segmentVertexOffset + 3) = ul.y
        vertices(segmentVertexOffset + 4) = ur.x
        vertices(segmentVertexOffset + 5) = ur.y
        vertices(segmentVertexOffset + 6) = lr.x
        vertices(segmentVertexOffset + 7) = lr.y
        segmentVertexOffset += 8
      }

      if (cornerImage.nonEmpty && (closed || !isFirst)) {

        val vUpUnnormalized = prevUp + nextUp
        val vUp = vUpUnnormalized / vUpUnnormalized.length
        val vRight = -vUp.orthogonal
        val radiusUp = vUp * width * 0.5f
        val radiusRight = vRight * width * 0.5f

        val ll = current - radiusUp - radiusRight
        val ul = current + radiusUp - radiusRight
        val ur = current + radiusUp + radiusRight
        val lr = current - radiusUp + radiusRight

        corners(cornerVertexOffset + 0) = ll.x
        corners(cornerVertexOffset + 1) = ll.y
        corners(cornerVertexOffset + 2) = ul.x
        corners(cornerVertexOffset + 3) = ul.y
        corners(cornerVertexOffset + 4) = ur.x
        corners(cornerVertexOffset + 5) = ur.y
        corners(cornerVertexOffset + 6) = lr.x
        corners(cornerVertexOffset + 7) = lr.y

        cornerVertexOffset += 8
      }

      isFirst = false
    }

    PreparedRepeatedImage(
      segments = vertices,
      segmentImage = lineImage,
      transform = transform,
      connections = corners,
      connectionImage = cornerImage
    )
  }

  private final def distance(p1: Vec2, p2: Vec2): Float = {
    val dx = p1.x - p2.x
    val dy = p1.y - p2.y
    math.sqrt(dx * dx + dy * dy).toFloat
  }
}

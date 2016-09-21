package se.gigurra.glasciia

/**
  * Created by johan on 2016-09-19.
  */
case class CameraConf(pos: Vec2[Float],
                      size: Float,
                      scaleType: ScaleType)

sealed trait ScaleType
case object ScaleX_Conformal extends ScaleType
case object ScaleY_Conformal extends ScaleType
case object ScaleXY extends ScaleType
case object WindowScale extends ScaleType

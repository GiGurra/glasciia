package com.github.gigurra.glasciia.test1.testcomponents

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.{BitmapFont, TextureRegion}
import com.github.gigurra.glasciia._
import com.github.gigurra.glasciia.Glasciia._
import com.github.gigurra.glasciia.impl.TextDrawer.Anchor
import com.github.gigurra.math.Vec2

import scala.util.Random

/**
  * Created by johan on 2016-10-08.
  */
object drawWorld {
  def apply(canvas: Canvas): Unit = {

    val app = canvas.game

    val monospaceFont = app.resource[BitmapFont]("font:monospace-default")
    val monospaceFontMasked = app.resource[BitmapFont]("font:monospace-default-masked")
    val walkingDudeAnimation = app.resource[Animation.Instance]("animation:capguy-walk:instance-0")
    val fillYellowTextureRegion = app.resource[TextureRegion]("image:fill-yellow")
    val testImage = app.resource[TextureRegion]("image:test-image")
    val effect1 = app.resource[ParticleSource]("particle-effect:test-effect:instance-0")
    val effect2 = app.resource[ParticleSource]("particle-effect:test-effect:instance-1")
    val effect3 = app.resource[ParticleSource]("particle-effect:test-effect:instance-2")
    val controlsInverted = app.getResource[Boolean]("controls-inverted").getOrElse(false)
    val background = app.resource[MultiLayer[TextureRegion]]("background-0")
    val mouseWorldPos = canvas.screen2World(canvas.mousePos)
    val cameraPos = canvas.cameraPos

    val camScale = 0.5f * (1.0f + math.min(canvas.width / 640.0f, canvas.height / 480.0f))
    canvas.drawFrame(
      pixelViewport = canvas.screenBounds,
      clearBuffer = Some(Color.DARK_GRAY),
      camPos = cameraPos,
      yDown = false,
      camViewportWithoutZoom = Vec2[Float](canvas.width, canvas.height) / camScale,
      setOrtho = true,
      useBatch = true
    ) {

      canvas.drawBackGround(background)

      canvas.drawText(
        text = "A",
        font = monospaceFont,
        color = Color.GREEN,
        at = Vec2(140, 140),
        rotate = 45,
        scale = 50
      )

      canvas.drawText(
        text = "B",
        font = monospaceFont,
        color = Color.GREEN,
        at = Vec2(240, 240),
        rotate = -45,
        scale = 50
      )

      canvas.drawText(
        text = "CDEFG",
        font = monospaceFont,
        color = Color.GREEN,
        at = Vec2(400, 400),
        rotate = 180 + canvas.drawTime * 360L / 1000L,
        scale = 50
      )

      canvas.drawText(
        text = "UL",
        font = monospaceFont,
        color = Color.RED,
        at = Vec2(0, 480),
        scale = 50
      )

      canvas.drawText(
        text = "LL",
        font = monospaceFont,
        color = Color.GREEN,
        at = Vec2(0, 50),
        scale = 50
      )

      if (controlsInverted) {
        canvas.drawText(
          text = "HAHA - CONTROLS INVERTED",
          font = monospaceFont,
          color = new Color(Random.nextFloat, Random.nextFloat, Random.nextFloat, 1.0f),
          at = Vec2(10, 80),
          scale = 40
        )
      }

      canvas.drawText(
        text = "UR",
        font = monospaceFont,
        color = Color.BLUE,
        at = Vec2(640 - 50 * 2 * monospaceFont.spaceWidth(), 480),
        scale = 50
      )

      canvas.drawText(
        text = "LR",
        font = monospaceFont,
        color = Color.BLACK,
        at = Vec2(640 - 50 * 2 * monospaceFont.spaceWidth(), 50),
        scale = 50
      )

      canvas.drawAnimation(
        animation = walkingDudeAnimation,
        at = Vec2(400, 100),
        scale = Vec2(120.0f, 200.0f)
      )

      canvas.drawImage(
        image = testImage,
        at = Vec2(100, 300),
        scale = Vec2(160.0f, 120.0f)
      )

      canvas.drawImage(
        image = fillYellowTextureRegion,
        at = canvas.cameraPos - canvas.cameraSize / 2.0f,
        scale = canvas.cameraSize
      )

      canvas.drawParticles(
        effect = effect1,
        at = testEffectPosition(canvas.drawTime)
      )

      canvas.drawParticles(
        effect = effect2,
        at = testEffectPosition(canvas.drawTime)
      )

      canvas.drawParticles(
        effect = effect3,
        at = mouseWorldPos,
        angle = canvas.drawTime * 180L / 1000L
      )

      canvas.drawText(
        text = "CAM",
        font = monospaceFont,
        color = Color.WHITE,
        anchor = Anchor.CC,
        scale = 25,
        at = cameraPos
      )

      canvas.drawText(
        text = "CAM",
        font = monospaceFontMasked,
        color = Color.WHITE,
        anchor = Anchor.CC,
        scale = 25,
        at = cameraPos - Vec2(0.0f, 50.0f)
      )

    }
  }

  private def testEffectPosition(tMillis: Long): Vec2[Float] = {
    Vec2[Float](320 + (tMillis % 5000L) * 0.05f, 240 / 2.0f)
  }
}

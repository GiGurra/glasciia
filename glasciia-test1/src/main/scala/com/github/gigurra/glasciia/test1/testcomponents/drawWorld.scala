package com.github.gigurra.glasciia.test1.testcomponents

import com.badlogic.gdx.Gdx
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
  def apply(canvas: Canvas, resources: ResourceManager): Unit = {

    val app = canvas.game

    val monospaceFont = resources[BitmapFont]("font:monospace-default")
    val monospaceFontMasked = resources[BitmapFont]("font:monospace-default-masked")
    val walkingDudeAnimation = resources[Animation.Instance]("animation:capguy-walk:instance-0")
    val fillYellowTextureRegion = resources[TextureRegion]("image:fill-yellow")
    val testImage = resources[TextureRegion]("image:test-image")
    val effect1 = resources[ParticleSource]("particle-effect:test-effect:instance-0")
    val effect2 = resources[ParticleSource]("particle-effect:test-effect:instance-1")
    val effect3 = resources[ParticleSource]("particle-effect:test-effect:instance-2")
    val controlsInverted = resources.get[Boolean]("controls-inverted").getOrElse(false)
    val background = resources[MultiLayer[TextureRegion]]("background-0")
    val mouseWorldPos = canvas.screen2World(canvas.mousePos)
    val cameraPos = canvas.cameraPos

    val camScale = 0.5f * (1.0f + math.min(canvas.width / 640.0f, canvas.height / 480.0f))
    canvas.drawFrame(
      pixelViewport = canvas.screenBounds,
      clearBuffer = Some(Color.DARK_GRAY),
      camPos = cameraPos,
      yDown = false,
      camViewportWithoutZoom = Vec2(canvas.width, canvas.height) / camScale,
      setOrtho = true,
      useBatch = true
    ) {

      canvas.drawBackGround(background)

      canvas.drawText(
        text = "A",
        font = monospaceFont,
        color = Color.GREEN,
        transform =
          Transform.
            translate(Vec2(140.0f, 140.0f))
            .rotate(45.0f)
            .scale(50, 50)
      )

      canvas.drawText(
        text = "B",
        font = monospaceFont,
        color = Color.GREEN,
        transform =
          Transform(
            at = Vec2(240.0f, 240.0f),
            angle = -45.0f,
            scale = Vec2(50, 50)
          )
      )

      canvas.drawText(
        text = "CDEFG",
        font = monospaceFont,
        color = Color.GREEN,
        transform = Transform(
          at = Vec2(400, 400),
          angle = 180 + canvas.drawTime * 360L / 1000L,
          scale = Vec2(50, 50)
        )
      )

      canvas.drawText(
        text = "UL",
        font = monospaceFont,
        color = Color.RED,
        transform = Transform(
          at = Vec2(0, 480),
          scale = Vec2(50, 50)
        )
      )

      canvas.drawText(
        text = "LL",
        font = monospaceFont,
        color = Color.GREEN,
        transform = Transform(
          at = Vec2(0, 50),
          scale = Vec2(50, 50)
        )
      )

      if (controlsInverted) {
        canvas.drawText(
          text = "HAHA - CONTROLS INVERTED",
          font = monospaceFont,
          color = new Color(Random.nextFloat, Random.nextFloat, Random.nextFloat, 1.0f),
          transform = Transform(
            at = Vec2(10, 80),
            scale = Vec2(40, 40)
          )
        )
      }

      canvas.drawText(
        text = "UR",
        font = monospaceFont,
        color = Color.BLUE,
        transform = Transform(
          at = Vec2(640 - 50 * 2 * monospaceFont.spaceWidth(), 480),
          scale = Vec2(50, 50)
        )
      )

      canvas.drawText(
        text = "LR",
        font = monospaceFont,
        color = Color.BLACK,
        transform = Transform(
          at = Vec2(640 - 50 * 2 * monospaceFont.spaceWidth(), 50),
          scale = Vec2(50, 50)
        )
      )

      canvas.drawAnimation(
        animation = walkingDudeAnimation,
        transform = Transform(
          at = Vec2(400.0f, 100.0f),
          scale = Vec2(120.0f, 200.0f)
        )
      )

      canvas.drawImage(
        image = testImage,
        transform =
          Transform
            .translate(Gdx.graphics.getFrameId % 320)
            .translate(Vec2(100, 300))
            .scale(1.0f, 1.0f)
            .rotate(Gdx.graphics.getFrameId % 320)
            .scale(Vec2(160.0f, 120.0f))
            .translate(-0.5f, -0.5f)
      )

      canvas.drawImage(
        image = testImage,
        transform =
          Transform
            .translate(Gdx.graphics.getFrameId % 320)
            .translate(Vec2(100, 300))
            .scale(1.0f, 1.0f)
            .rotate(Gdx.graphics.getFrameId % 320)
            .scale(Vec2(160.0f, 120.0f))
            .translate(-0.5f, -0.5f)
      )
/*
      canvas.drawImage(
        image = fillYellowTextureRegion,
        transform = Transform(
          at = canvas.cameraPos - canvas.cameraSize / 2.0f,
          scale = canvas.cameraSize
        )
      )
*/
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
        transform = Transform(
          at = cameraPos,
          scale = Vec2(25, 25)
        )
      )

      canvas.drawText(
        text = "CAM",
        font = monospaceFontMasked,
        color = Color.WHITE,
        anchor = Anchor.CC,
        transform = Transform(
          at = cameraPos - Vec2(0.0f, 50.0f),
          scale = Vec2(25, 25)
        )
      )
    }
  }

  private def testEffectPosition(tMillis: Long): Vec2 = {
    Vec2(320 + (tMillis % 5000L) * 0.05f, 240 / 2.0f)
  }
}

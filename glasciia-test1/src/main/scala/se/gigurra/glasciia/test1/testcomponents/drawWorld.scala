package se.gigurra.glasciia.test1.testcomponents

import com.badlogic.gdx.graphics.Color
import se.gigurra.glasciia._
import se.gigurra.glasciia.impl.TextDrawer.Anchor
import se.gigurra.math.Vec2

/**
  * Created by johan on 2016-10-08.
  */
object drawWorld {
  def apply(canvas: Canvas): Unit = {

    val app = canvas.app

    val monospaceFont = app.resource[Font]("font:monospace-default")
    val monospaceFontMasked = app.resource[Font]("font:monospace-default-masked")
    val walkingDudeAnimation = app.resource[Animation.Instance]("animation:capguy-walk:instance-0")
    val testImage = app.resource[StaticImage]("image:test-image")
    val effect1 = app.resource[ParticleSource]("particle-effect:test-effect:instance-0")
    val effect2 = app.resource[ParticleSource]("particle-effect:test-effect:instance-1")
    val effect3 = app.resource[ParticleSource]("particle-effect:test-effect:instance-2")

    val cameraPos = app.resource[Vec2[Float]]("camera-position")
    val mouseWorldPos = canvas.screen2World(canvas.mousePos)

    val background = app.resource[MultiLayer[Image]]("background-0")

    canvas.setOrtho(
      yDown = false,
      width = 480 * canvas.aspectRatio,
      height = 480,
      scaling = math.max(1.0f, 0.5f * (1.0f + math.min(canvas.width.toFloat / 480.0f, canvas.height.toFloat / 480.0f)))
    )

    canvas.drawFrame(
      background = Color.DARK_GRAY,
      camPos = cameraPos) {

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
        rotate = 180 + canvas.drawTime.toFloat * 360.0f,
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
        scale = Vec2(120.0f, 120.0f)
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
        angle = canvas.drawTime.toFloat * 180.0f
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

  private def testEffectPosition(tSec: Double): Vec2[Float] = {
    Vec2[Float](320 + ((tSec * 1000.0).toLong % 5000L) * 0.05f, 240 / 2.0f)
  }
}

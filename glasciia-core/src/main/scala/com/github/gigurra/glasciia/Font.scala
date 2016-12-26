package com.github.gigurra.glasciia

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.BitmapFont.BitmapFontData
import com.badlogic.gdx.graphics.g2d._
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter

object Font {

  val DEFAULT_MASK_CHAR: Char = 0x2022
  val DEFAULT_EXTRA_CHARACTERS = Vector(DEFAULT_MASK_CHAR).mkString

  /**
   * See https://github.com/libgdx/libgdx/wiki/Gdx-freetype
   * For parameter descriptions
   */
  def fromTtfFile(source: FileHandle,
                  size: Int = 20,
                  color: Color = Color.WHITE,
                  borderWidth: Float = 0.0f,
                  borderColor: Color = Color.BLACK,
                  borderStraight: Boolean = false,
                  shadowOffsetX: Int = 0,
                  shadowOffsetY: Int = 0,
                  shadowColor: Color = new Color(0, 0, 0, 0.75f),
                  characters: String = FreeTypeFontGenerator.DEFAULT_CHARS,
                  kerning: Boolean = true,
                  packer: PixmapPacker = null,
                  flip: Boolean = false,
                  genMipMaps: Boolean = false,
                  mask: Option[Char] = None,
                  loadExtraCharacters: String = DEFAULT_EXTRA_CHARACTERS,
                  minFilter: TextureFilter = TextureFilter.Linear,
                  magFilter: TextureFilter = TextureFilter.Linear): BitmapFont = {

    val generator = new FreeTypeFontGenerator(source)
    val parameter = new FreeTypeFontParameter()

    parameter.size = size
    parameter.color = color
    parameter.borderWidth = borderWidth
    parameter.borderColor = borderColor
    parameter.borderStraight = borderStraight
    parameter.shadowOffsetX = shadowOffsetX
    parameter.shadowOffsetY = shadowOffsetY
    parameter.shadowColor = shadowColor
    parameter.characters = characters
    parameter.kerning = kerning
    parameter.packer = packer
    parameter.flip = flip
    parameter.genMipMaps = genMipMaps
    parameter.minFilter = minFilter
    parameter.magFilter = magFilter
    parameter.characters += loadExtraCharacters

    val bitmapFontUnmasked = generator.generateFont(parameter)
    generator.dispose()

    val bitmapFont = mask match {
      case None => bitmapFontUnmasked
      case Some(maskChar) => createMaskedFont(bitmapFontUnmasked, maskChar, ownsTexture = true)
    }

    bitmapFont.setUseIntegerPositions(false)
    bitmapFont
  }

  def createMaskedFont(source: BitmapFont, maskChar: Char, ownsTexture: Boolean): BitmapFont = {

    val out = new BitmapFont(
      createMaskedFontData(source.getData, maskChar),
      source.getRegions,
      source.usesIntegerPositions
    )

    if (ownsTexture) {
      source.dispose()
      out.setOwnsTexture(true)
    }

    out
  }

  def createMaskedFontData(source: BitmapFontData, maskChar: Char): BitmapFontData = {
    require(source.hasGlyph(maskChar), s"Cannot mask font $source with character '$maskChar', since that character doesn't exist in the font!")

    val out = new BitmapFontData
    out.imagePaths = source.imagePaths
    out.fontFile = source.fontFile
    out.flipped = source.flipped

    out.padTop = source.padTop
    out.padRight = source.padRight
    out.padBottom = source.padBottom
    out.padLeft = source.padLeft

    out.lineHeight = source.lineHeight
    out.capHeight = source.capHeight

    out.ascent = source.ascent
    out.descent = source.descent
    out.down = source.down

    out.scaleX = source.scaleX
    out.scaleY = source.scaleY

    out.markupEnabled = source.markupEnabled
    out.cursorX = source.cursorX
    out.missingGlyph = source.getGlyph(maskChar)

    out.spaceWidth = source.spaceWidth
    out.xHeight = source.xHeight
    out.breakChars = source.breakChars
    out.xChars = source.xChars
    out.capChars = source.capChars

    out
  }
}

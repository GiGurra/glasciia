package se.gigurra.glasciia

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter
import com.badlogic.gdx.graphics.g2d._
import com.badlogic.gdx.utils.Align
import se.gigurra.math.{Vec2, Zero}
import Glasciia._
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g2d.BitmapFont.{BitmapFontData, Glyph}

case class Font(font: BitmapFont, size: Float)  {

  def preload(str: CharSequence,
              at: Vec2[Float] = Zero.vec2f,
              align: Int = Align.left,
              targetWidth: Float = 0.0f,
              wrap: Boolean = false,
              color: Color = null,
              alphaScale: Float = 1.0f): BitmapFontCache = {
    val cached = font.newFontCache()
    cached.addText(new GlyphLayout(
      font,
      str,
      Option(color).getOrElse(font.getColor).scaleAlpha(alphaScale),
      targetWidth,
      align,
      wrap), at.x, at.y)
    cached
  }

  def spaceWidth(normalized: Boolean = true): Float = {
    if (normalized) font.getSpaceWidth / size
    else font.getSpaceWidth
  }

  def heightOf(lines: Seq[String], normalized: Boolean = true): Float = {
    val numLines = lines.size
    val heightPerLine = size
    val out = heightPerLine * numLines
    if (normalized) out / size
    else out
  }

  def lineHeight(normalized: Boolean = true): Float = {
    if (normalized) font.getLineHeight / size
    else font.getLineHeight
  }

  def close(): Unit = {
    font.dispose()
  }

  def createMaskedInstance(maskChar: Char, deleteSource: Boolean = false): Font = {
    new Font(Font.createMaskedFont(font, maskChar, deleteSource = deleteSource), size)
  }
}

object Font {


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
                  minFilter: TextureFilter = TextureFilter.Linear,
                  magFilter: TextureFilter = TextureFilter.Linear): Font = {

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

    val bitmapFontUnmasked = generator.generateFont(parameter)
    generator.dispose()

    val bitmapFont = mask match {
      case None => bitmapFontUnmasked
      case Some(maskChar) => createMaskedFont(bitmapFontUnmasked, maskChar, deleteSource = true)
    }

    bitmapFont.setUseIntegerPositions(false)
    new Font(bitmapFont, size.toFloat)
  }

  def createMaskedFont(source: BitmapFont, maskChar: Char, deleteSource: Boolean): BitmapFont = {

    val out = new BitmapFont(
      copyFontdata(source.getData),
      source.getRegions,
      source.usesIntegerPositions
    )

    if (deleteSource) {
      source.dispose()
      out.setOwnsTexture(true)
    }

    maskFontData(out.getData, maskChar)

    out
  }

  def maskFontData(data: BitmapFontData, maskChar: Char): Unit = {
    require(data.hasGlyph(maskChar), s"Cannot mask font $data with character '$maskChar', since that character doesn't exist in the font!")
    val maskGlyph = data.getGlyph(maskChar)
    for {
      glyphPage <- data.glyphs if glyphPage != null
      glyph <- glyphPage if glyph != null
    } {
      data.setGlyph(glyph.id, maskGlyph)
    }
    Option(data.missingGlyph).foreach(mg => data.setGlyph(mg.id, maskGlyph))
  }

  def copyFontdata(source: BitmapFontData): BitmapFontData = {
    /**
      *
		public final Glyph[][] glyphs = new Glyph[PAGES][];
      */
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
    out.missingGlyph = source.missingGlyph

    out.spaceWidth = source.spaceWidth
    out.xHeight = source.xHeight
    out.breakChars = source.breakChars
    out.xChars = source.xChars
    out.capChars = source.capChars

    for {
      glyphPage <- source.glyphs if glyphPage != null
      glyph <- glyphPage if glyph != null
    } {
      out.setGlyph(glyph.id, glyph)
    }

    out
  }

  import scala.language.implicitConversions
  implicit def toFont(font: Font): BitmapFont = font.font
}

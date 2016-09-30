package org.apache.pdfbox.rendering;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.fontbox.ttf.GlyphData;
import org.apache.fontbox.ttf.HeaderTable;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDCIDFontType2;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.util.awt.AffineTransform;

import android.graphics.Path;
import android.util.Log;

/**
 * This class provides a glyph to GeneralPath conversion for TrueType fonts.
 */
final class TTFGlyph2D implements Glyph2D
{
	private final PDFont font;
	private final TrueTypeFont ttf;
	private float scale = 1.0f;
	private boolean hasScaling;
	private final Map<Integer, Path> glyphs = new HashMap<Integer, Path>();
	private final boolean isCIDFont;
	/**
	 * Constructor.
	 *
	 * @param ttfFont TrueType font
	 */
	public TTFGlyph2D(PDTrueTypeFont ttfFont) throws IOException
	{
		this(ttfFont.getTrueTypeFont(), ttfFont, false);
	}
	/**
	 * Constructor.
	 *
	 * @param type0Font Type0 font, with CIDFontType2 descendant
	 */
	public TTFGlyph2D(PDType0Font type0Font) throws IOException
	{
		this(((PDCIDFontType2)type0Font.getDescendantFont()).getTrueTypeFont(), type0Font, true);
	}
	public TTFGlyph2D(TrueTypeFont ttf, PDFont font, boolean isCIDFont)
			throws IOException
	{
		this.font = font;
		this.ttf = ttf;
		this.isCIDFont = isCIDFont;
		// get units per em, which is used as scaling factor
		HeaderTable header = this.ttf.getHeader();
		if (header != null && header.getUnitsPerEm() != 1000)
		{
			// in most case the scaling factor is set to 1.0f
			// due to the fact that units per em is set to 1000
			scale = 1000f / header.getUnitsPerEm();
			hasScaling = true;
		}
	}
	@Override
	public Path getPathForCharacterCode(int code) throws IOException
	{
		int gid = getGIDForCharacterCode(code);
		return getPathForGID(gid, code);
	}
	// Try to map the given code to the corresponding glyph-ID
	private int getGIDForCharacterCode(int code) throws IOException
	{
		if (isCIDFont)
		{
			return ((PDType0Font)font).codeToGID(code);
		}
		else
		{
			return ((PDTrueTypeFont)font).codeToGID(code);
		}
	}
	/**
	 * Returns the path describing the glyph for the given glyphId.
	 *
	 * @param gid the GID
	 * @param code the character code
	 *
	 * @return the GeneralPath for the given glyphId
	 */
	public Path getPathForGID(int gid, int code) throws IOException
	{
		Path glyphPath;
		if (glyphs.containsKey(gid))
		{
			glyphPath = glyphs.get(gid);
		}
		else
		{
			if (gid == 0 || gid >= ttf.getMaximumProfile().getNumGlyphs())
			{
				if (isCIDFont)
				{
					int cid = ((PDType0Font) font).codeToCID(code);
					String cidHex = String.format("%04x", cid);
					Log.w("PdfBoxAndroid", "No glyph for " + code + " (CID " + cidHex + ") in font " +
							font.getName());
				}
				else
				{
					Log.w("PdfBoxAndroid", "No glyph for " + code + " in font " + font.getName());
				}
			}
			GlyphData glyph = ttf.getGlyph().getGlyph(gid);
			// Acrobat only draws GID 0 for embedded or "Standard 14" fonts, see PDFBOX-2372
			if (gid == 0 && !font.isEmbedded() && !font.isStandard14())
			{
				glyph = null;
			}
			if (glyph == null)
			{
				// empty glyph (e.g. space, newline)
				glyphPath = new Path();
				glyphs.put(gid, glyphPath);
			}
			else
			{
				glyphPath = glyph.getPath();
				if (hasScaling)
				{
					AffineTransform atScale = AffineTransform.getScaleInstance(scale, scale);
					glyphPath.transform(atScale.toMatrix());
				}
				glyphs.put(gid, glyphPath);
			}
		}
		return glyphPath != null ? new Path(glyphPath) : null; // todo: expensive
	}
	@Override
	public void dispose()
	{
		glyphs.clear();
	}
}

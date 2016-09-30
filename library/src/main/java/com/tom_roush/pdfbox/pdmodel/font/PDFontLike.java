package com.tom_roush.pdfbox.pdmodel.font;

import java.io.IOException;
import com.tom_roush.fontbox.util.BoundingBox;
import com.tom_roush.pdfbox.util.Matrix;
import com.tom_roush.pdfbox.util.Vector;

/**
 * A font-like object.
 *
 * @author John Hewson
 */
public interface PDFontLike
{
	/**
	 * Returns the name of this font, either the PostScript "BaseName" or the Type 3 "Name".
	 */
	String getName();

	/**
	 * Returns the font descriptor, may be null.
	 */
	PDFontDescriptor getFontDescriptor();

	/**
	 * Returns the font matrix, which represents the transformation from glyph space to text space.
	 */
	Matrix getFontMatrix();

	/**
	 * Returns the font's bounding box.
	 */
	BoundingBox getBoundingBox() throws IOException;

	/**
	 * Returns the position vector (v), in text space, for the given character.
	 * This represents the position of vertical origin relative to horizontal origin, for
	 * horizontal writing it will always be (0, 0). For vertical writing both x and y are set.
	 *
	 * @param code character code
	 * @return position vector
	 */
	Vector getPositionVector(int code);

	/**
	 * Returns the height of the given character, in glyph space. This can be expensive to
	 * calculate. Results are only approximate.
	 *
	 * @param code character code
	 */
	    float getHeight(int code) throws IOException;

	/**
	 * Returns the advance width of the given character, in glyph space.
	 *
	 * @param code character code
	 */
	float getWidth(int code) throws IOException;

	/**
	 * Returns the width of a glyph in the embedded font file.
	 *
	 * @param code character code
	 * @return width in glyph space
	 * @throws IOException if the font could not be read
	 */
	float getWidthFromFont(int code) throws IOException;

	/**
	 * Returns true if the font file is embedded in the PDF.
	 */
	boolean isEmbedded();

	/**
	 * Returns true if the embedded font file is damaged.
	 */
	boolean isDamaged();

	/**
	 * This will get the average font width for all characters.
	 *
	 * @return The width is in 1000 unit of text space, ie 333 or 777
	 */
	// todo: this method is highly suspicious, the average glyph width is not usually a good metric
	float getAverageFontWidth();
}

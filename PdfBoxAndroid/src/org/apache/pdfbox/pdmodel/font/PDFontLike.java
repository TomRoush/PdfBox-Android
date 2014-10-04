package org.apache.pdfbox.pdmodel.font;

import java.io.IOException;
import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.Vector;

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
    public String getName();

    /**
     * Returns the font descriptor, may be null.
     */
    public PDFontDescriptor getFontDescriptor();

    /**
     * Returns the font matrix, which represents the transformation from glyph space to text space.
     */
    public Matrix getFontMatrix();

    /**
     * Returns the font's bounding box.
     */
    public abstract BoundingBox getBoundingBox() throws IOException;

    /**
     * Returns the position vector (v), in text space, for the given character.
     * This represents the position of vertical origin relative to horizontal origin, for
     * horizontal writing it will always be (0, 0). For vertical writing both x and y are set.
     *
     * @param code character code
     * @return position vector
     */
    public Vector getPositionVector(int code);

    /**
     * Returns the height of the given character, in glyph space. This can be expensive to
     * calculate. Results are only approximate.
     *
     * @param code character code
     */
//    public abstract float getHeight(int code) throws IOException;TODO

    /**
     * Returns the advance width of the given character, in glyph space.
     *
     * @param code character code
     */
    public float getWidth(int code) throws IOException;

    /**
     * Returns the width of a glyph in the embedded font file.
     *
     * @param code character code
     * @return width in glyph space
     * @throws IOException if the font could not be read
     */
    public abstract float getWidthFromFont(int code) throws IOException;

    /**
     * Returns true if the font file is embedded in the PDF.
     */
    public abstract boolean isEmbedded();

    /**
     * This will get the average font width for all characters.
     *
     * @return The width is in 1000 unit of text space, ie 333 or 777
     */
    // todo: this method is highly suspicious, the average glyph width is not usually a good metric
    public float getAverageFontWidth();
}

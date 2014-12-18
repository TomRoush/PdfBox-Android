package org.apache.pdfbox.pdmodel.font;

import java.io.IOException;

import org.apache.fontbox.ttf.Type1Equivalent;

import android.graphics.Path;

/**
 * A Type 1-equivalent font in a PDF, i.e. a font which can access glyphs by their PostScript name.
 * May be a PFB, CFF, or TTF.
 *
 * @author John Hewson
 */
public interface PDType1Equivalent extends PDFontLike
{
    /**
     * Returns the name of this font.
     */
    public String getName();

    /**
     * Returns the glyph name for the given character code.
     *
     * @param code character code
     * @return PostScript glyph name
     */
    public String codeToName(int code) throws IOException;

    /**
     * Returns the glyph path for the given character code.
     * @param name PostScript glyph name
     * @throws java.io.IOException if the font could not be read
     */
    public Path getPath(String name) throws IOException;

    /**
     * Returns the embedded or system font for rendering. This font is a Type 1-equivalent, but
     * may not be a Type 1 font, it could be a CFF font or TTF font. If there is no suitable font
     * then the fallback font will be returned: this method never returns null.
     */
    public Type1Equivalent getType1Equivalent();
}

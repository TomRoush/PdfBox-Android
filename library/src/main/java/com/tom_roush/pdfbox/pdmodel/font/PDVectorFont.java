package com.tom_roush.pdfbox.pdmodel.font;

import android.graphics.Path;

import java.io.IOException;

/**
 * A vector outline font, e.g. not Type 3.
 *
 * @author John Hewson
 */
public interface PDVectorFont
{
    /**
     * Returns the glyph path for the given character code.
     *
     * @param code character code
     * @throws java.io.IOException if the font could not be read
     */
    Path getPath(int code) throws IOException;

    /**
     * Returns true if this font contains a glyph for the given character code.
     *
     * @param code character code
     */
    boolean hasGlyph(int code) throws IOException;
}

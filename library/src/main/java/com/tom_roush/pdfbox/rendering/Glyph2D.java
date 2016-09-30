package com.tom_roush.pdfbox.rendering;

import java.io.IOException;

import android.graphics.Path;

/**
 * This interface is implemented by several font specific classes which is called to get the
 * general path of a single glyph of the represented font most likely to render it.
 */
interface Glyph2D {
	/**
	 * Returns the path describing the glyph for the given character code.
	 *
	 * @param code the character code
	 *
	 * @return the GeneralPath for the given character code
	 */
	Path getPathForCharacterCode(int code) throws IOException;
	/**
	 * Remove all cached resources.
	 */
	void dispose();
}

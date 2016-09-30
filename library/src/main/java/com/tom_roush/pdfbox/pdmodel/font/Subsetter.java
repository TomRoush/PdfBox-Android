package com.tom_roush.pdfbox.pdmodel.font;

import java.io.IOException;

/**
 * Interface for a font subsetter.
 */
interface Subsetter
{
	/**
	 * Adds the given Unicode code point to this subset.
	 * 
	 * @param codePoint Unicode code point
	 */
	void addToSubset(int codePoint);
	
	/**
	 * Subset this font now.
	 * 
	 * @throws IOException if the font could not be read
	 */
	void subset() throws IOException;
}
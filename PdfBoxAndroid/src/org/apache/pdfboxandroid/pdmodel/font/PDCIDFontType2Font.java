package org.apache.pdfboxandroid.pdmodel.font;

import org.apache.pdfboxandroid.cos.COSDictionary;

/**
 * This is implementation of the CIDFontType2 Font.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * 
 */
public class PDCIDFontType2Font extends PDCIDFont {
	/**
     * Constructor.
     *
     * @param fontDictionary The font dictionary according to the PDF specification.
     */
    public PDCIDFontType2Font( COSDictionary fontDictionary )
    {
        super( fontDictionary );
    }
}

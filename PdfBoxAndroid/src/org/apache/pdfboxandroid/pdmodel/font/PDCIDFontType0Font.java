package org.apache.pdfboxandroid.pdmodel.font;

import org.apache.pdfboxandroid.cos.COSDictionary;

/**
 * This is implementation of the CIDFontType0 Font.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.6 $
 */
public class PDCIDFontType0Font extends PDCIDFont {
	/**
     * Constructor.
     *
     * @param fontDictionary The font dictionary according to the PDF specification.
     */
    public PDCIDFontType0Font( COSDictionary fontDictionary )
    {
        super( fontDictionary );
    }
}

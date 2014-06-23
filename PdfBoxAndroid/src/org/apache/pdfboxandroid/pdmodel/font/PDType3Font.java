package org.apache.pdfboxandroid.pdmodel.font;

import org.apache.pdfboxandroid.cos.COSDictionary;

/**
 * This is implementation of the Type3 Font.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.8 $
 */
public class PDType3Font extends PDSimpleFont {
	/**
     * Constructor.
     *
     * @param fontDictionary The font dictionary according to the PDF specification.
     */
    public PDType3Font( COSDictionary fontDictionary )
    {
        super( fontDictionary );
    }
}

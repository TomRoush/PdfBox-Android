package org.apache.pdfboxandroid.pdmodel.font;

import java.io.IOException;

import org.apache.pdfboxandroid.PDFBox;
import org.apache.pdfboxandroid.cos.COSDictionary;
import org.apache.pdfboxandroid.pdmodel.common.PDStream;

import android.util.Log;

/**
 * This is implementation of the Type1 Font.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.11 $
 */
public class PDType1Font extends PDSimpleFont {
	private PDType1CFont type1CFont = null;
	
	
	/**
     * Constructor.
     *
     * @param fontDictionary The font dictionary according to the PDF specification.
     */
    public PDType1Font( COSDictionary fontDictionary )
    {
        super( fontDictionary );
        PDFontDescriptor fd = getFontDescriptor();
        if (fd != null && fd instanceof PDFontDescriptorDictionary)
        {
            // a Type1 font may contain a Type1C font
            PDStream fontFile3 = ((PDFontDescriptorDictionary)fd).getFontFile3();
            if (fontFile3 != null)
            {
                try 
                {
                    type1CFont = new PDType1CFont( super.font );
                }
                catch (IOException exception) 
                {
                    Log.i(PDFBox.LOG_TAG, "Can't read the embedded type1C font " + fd.getFontName() );
                }
            }
        }
    }
}

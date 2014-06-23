package org.apache.pdfboxandroid.pdmodel.font;

import java.io.IOException;

import org.apache.pdfboxandroid.PDFBox;
import org.apache.pdfboxandroid.cos.COSArray;
import org.apache.pdfboxandroid.cos.COSDictionary;
import org.apache.pdfboxandroid.cos.COSName;

import android.util.Log;

/**
 * This is implementation of the Type0 Font.
 * See <a href="https://issues.apache.org/jira/browse/PDFBOX-605">PDFBOX-605</a>
 * for the related improvement issue.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.9 $
 */
public class PDType0Font extends PDSimpleFont {
	private COSArray descendantFontArray;
    private PDFont descendantFont;
    private COSDictionary descendantFontDictionary;
	
	/**
     * Constructor.
     *
     * @param fontDictionary The font dictionary according to the PDF specification.
     */
    public PDType0Font( COSDictionary fontDictionary )
    {
        super( fontDictionary );
        descendantFontDictionary = (COSDictionary)getDescendantFonts().getObject( 0 );
        if (descendantFontDictionary != null)
        {
            try
            {
                descendantFont = PDFontFactory.createFont( descendantFontDictionary );
            }
            catch (IOException exception)
            {
                Log.e(PDFBox.LOG_TAG, "Error while creating the descendant font!");
            }
        }
    }
    
    private COSArray getDescendantFonts()
    {
        if (descendantFontArray == null)
        {
            descendantFontArray = (COSArray)font.getDictionaryObject( COSName.DESCENDANT_FONTS );
        }
        return descendantFontArray;
    }
}

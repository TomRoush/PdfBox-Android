package org.apache.pdfboxandroid.pdmodel.font;

import java.io.IOException;

import org.apache.pdfboxandroid.PDFBox;
import org.apache.pdfboxandroid.cos.COSDictionary;
import org.apache.pdfboxandroid.cos.COSName;

import android.util.Log;

/**
 * This will create the correct type of font based on information in the dictionary.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.6 $
 */
public class PDFontFactory {
	/**
     * This will create the correct font based on information in the dictionary.
     *
     * @param dic The populated dictionary.
     *
     * @return The corrent implementation for the font.
     *
     * @throws IOException If the dictionary is not valid.
     */
    public static PDFont createFont( COSDictionary dic ) throws IOException
    {
        PDFont retval = null;

        COSName type = (COSName)dic.getDictionaryObject( COSName.TYPE );
        if( type != null && !COSName.FONT.equals( type ) )
        {
            throw new IOException( "Cannot create font if /Type is not /Font.  Actual=" +type );
        }

        COSName subType = (COSName)dic.getDictionaryObject( COSName.SUBTYPE );
        if (subType == null) 
        {
            throw new IOException( "Cannot create font as /SubType is not set." );
        }
        if( subType.equals( COSName.TYPE1) )
        {
            retval = new PDType1Font( dic );
        }
        else if( subType.equals( COSName.MM_TYPE1 ) )
        {
            retval = new PDMMType1Font( dic );
        }
        else if( subType.equals( COSName.TRUE_TYPE ) )
        {
            retval = new PDTrueTypeFont( dic );
        }
        else if( subType.equals( COSName.TYPE3 ) )
        {
            retval = new PDType3Font( dic );
        }
        else if( subType.equals( COSName.TYPE0 ) )
        {
            retval = new PDType0Font( dic );
        }
        else if( subType.equals( COSName.CID_FONT_TYPE0 ) )
        {
            retval = new PDCIDFontType0Font( dic );
        }
        else if( subType.equals( COSName.CID_FONT_TYPE2 ) )
        {
            retval = new PDCIDFontType2Font( dic );
        }
        else
        {
            Log.w(PDFBox.LOG_TAG, "Substituting TrueType for unknown font subtype=" + subType.getName());
            retval = new PDTrueTypeFont( dic );
        }
        return retval;
    }
}

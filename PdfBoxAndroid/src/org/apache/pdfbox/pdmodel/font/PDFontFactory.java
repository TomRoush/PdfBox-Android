package org.apache.pdfbox.pdmodel.font;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;

/**
 * This will create the correct type of font based on information in the dictionary.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.6 $
 */
public class PDFontFactory
{
    /**
     * private constructor, should only use static methods in this class.
     */
    private PDFontFactory()
    {
    }

    /**
     * Logger instance.
     */
    private static final Log LOG = LogFactory.getLog(PDFontFactory.class);
    
    /**
     * This will create the correct font based on information in the dictionary.
     *
     * @param dic The populated dictionary.
     *
     * @param fontCache A Map to cache already created fonts
     *
     * @return The corrent implementation for the font.
     *
     * @throws IOException If the dictionary is not valid.
     * 
     * @deprecated due to some side effects font caching is no longer supported, 
     * use {@link #createFont(COSDictionary)} instead
     */
    public static PDFont createFont(COSDictionary dic, Map fontCache) throws IOException
    {
        return createFont(dic);
    }

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
//            retval = new PDMMType1Font( dic );
        }
        else if( subType.equals( COSName.TRUE_TYPE ) )
        {
//            retval = new PDTrueTypeFont( dic );
        }
        else if( subType.equals( COSName.TYPE3 ) )
        {
//            retval = new PDType3Font( dic );
        }
        else if( subType.equals( COSName.TYPE0 ) )
        {
//            retval = new PDType0Font( dic );
        }
        else if( subType.equals( COSName.CID_FONT_TYPE0 ) )
        {
//            retval = new PDCIDFontType0Font( dic );
        }
        else if( subType.equals( COSName.CID_FONT_TYPE2 ) )
        {
//            retval = new PDCIDFontType2Font( dic );
        }
        else
        {
            // assuming Type 1 font (see PDFBOX-1988) because it seems that Adobe Reader does this
            // however, we may need more sophisticated logic perhaps looking at the FontFile
            LOG.warn( "Invalid font subtype '" + subType.getName() + "'" );
            return new PDType1Font( dic );
        }
        return retval;
    }
}

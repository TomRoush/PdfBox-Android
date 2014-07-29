package org.apache.pdfbox.pdmodel.font;

import java.io.IOException;

import org.apache.fontbox.afm.FontMetric;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.encoding.AFMEncoding;
import org.apache.pdfbox.encoding.Encoding;
import org.apache.pdfbox.pdmodel.common.PDStream;

/**
 * This is implementation of the Type1 Font.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.11 $
 */
public class PDType1Font extends PDSimpleFont
{

	/**
     * Constructor.
     */
    public PDType1Font()
    {
        super();
        font.setItem( COSName.SUBTYPE, COSName.TYPE1 );
    }
    
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
                    log.info("Can't read the embedded type1C font " + fd.getFontName() );
                }
            }
        }
    }
    
    protected void determineEncoding()
    {
        super.determineEncoding();
        Encoding fontEncoding = getFontEncoding();
        if(fontEncoding == null)
        {
            FontMetric metric = getAFM();
            if (metric != null)
            {
                fontEncoding = new AFMEncoding( metric );
            }
            setFontEncoding(fontEncoding);
        }
        getEncodingFromFont(getFontEncoding() == null);
    }

}

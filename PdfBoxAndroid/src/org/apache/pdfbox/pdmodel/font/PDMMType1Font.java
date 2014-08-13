package org.apache.pdfbox.pdmodel.font;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;

/**
 * This is implementation of the Multiple Master Type1 Font.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.4 $
 */
public class PDMMType1Font extends PDType1Font
{
    /**
     * Constructor.
     */
    public PDMMType1Font()
    {
        super();
        font.setItem( COSName.SUBTYPE, COSName.MM_TYPE1 );
    }

    /**
     * Constructor.
     *
     * @param fontDictionary The font dictionary according to the PDF specification.
     */
    public PDMMType1Font( COSDictionary fontDictionary )
    {
        super( fontDictionary );
    }
}

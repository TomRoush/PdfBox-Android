package org.apache.pdfbox.pdmodel.font;

import java.io.IOException;

import org.apache.pdfbox.cos.COSDictionary;

/**
 * Type 1 Multiple Master Font.
 *
 * @author Ben Litchfield
 */
public class PDMMType1Font extends PDType1Font
{
    /**
     * Creates an MMType1Font from a Font dictionary in a PDF.
     *
     * @param fontDictionary font dictionary
     * @param glyphList a custom glyph list for Unicode mapping
     */
    public PDMMType1Font(COSDictionary fontDictionary) throws IOException
    {
        super(fontDictionary);
    }
}

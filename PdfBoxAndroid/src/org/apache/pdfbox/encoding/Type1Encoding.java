package org.apache.pdfbox.encoding;

import org.apache.pdfbox.cos.COSBase;

/**
 * This class represents an encoding which was read from a type1 font.
 * 
 */
public class Type1Encoding extends Encoding
{
    public Type1Encoding(int size)
    {
        for (int i=1;i<size;i++)
        {
            addCharacterEncoding(i, NOTDEF);
        }
    }

    /**
     * {@inheritDoc}
     */
    public COSBase getCOSObject()
    {
        return null;
    }

}

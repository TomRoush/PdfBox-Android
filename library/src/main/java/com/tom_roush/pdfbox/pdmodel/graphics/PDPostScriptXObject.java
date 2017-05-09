package com.tom_roush.pdfbox.pdmodel.graphics;

import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.cos.COSStream;

/**
 * A PostScript XObject.
 * Conforming readers may not be able to interpret the PostScript fragments.
 *
 * @author John Hewson
 */
public class PDPostScriptXObject extends PDXObject
{
    /**
     * Creates a PostScript XObject.
     * @param stream The XObject stream
     */
    public PDPostScriptXObject(COSStream stream)
    {
        super(stream, COSName.PS);
    }
}

package com.tom_roush.pdfbox.pdmodel.graphics.shading;

import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSName;

/**
 * Resources for a shading type 4 (Free-Form Gouraud-Shaded Triangle Mesh).
 */
public class PDShadingType4 extends PDTriangleBasedShadingType
{
    /**
     * Constructor using the given shading dictionary.
     *
     * @param shadingDictionary the dictionary for this shading
     */
    public PDShadingType4(COSDictionary shadingDictionary)
    {
        super(shadingDictionary);
    }

    @Override
    public int getShadingType()
    {
        return PDShading.SHADING_TYPE4;
    }

    /**
     * The bits per flag of this shading. This will return -1 if one has not
     * been set.
     *
     * @return The number of bits per flag.
     */
    public int getBitsPerFlag()
    {
        return getCOSObject().getInt(COSName.BITS_PER_FLAG, -1);
    }

    /**
     * Set the number of bits per flag.
     *
     * @param bitsPerFlag the number of bits per flag
     */
    public void setBitsPerFlag(int bitsPerFlag)
    {
        getCOSObject().setInt(COSName.BITS_PER_FLAG, bitsPerFlag);
    }

//    @Override
//    public Paint toPaint(Matrix matrix)
//    {
//        return new Type4ShadingPaint(this, matrix);
//    }TODO: PdfBox-Android
}

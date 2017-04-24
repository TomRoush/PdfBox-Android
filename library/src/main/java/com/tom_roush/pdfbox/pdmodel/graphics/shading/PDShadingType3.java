package com.tom_roush.pdfbox.pdmodel.graphics.shading;

import com.tom_roush.pdfbox.cos.COSDictionary;

/**
 * Resources for a radial shading.
 */
public class PDShadingType3 extends PDShadingType2
{
    /**
     * Constructor using the given shading dictionary.
     *
     * @param shadingDictionary the dictionary for this shading
     */
    public PDShadingType3(COSDictionary shadingDictionary)
    {
        super(shadingDictionary);
    }

    @Override
    public int getShadingType()
    {
        return PDShading.SHADING_TYPE3;
    }

//    @Override
//    public Paint toPaint(Matrix matrix)
//    {
//        return new RadialShadingPaint(this, matrix);
//    }TODO: PdfBox-Android
}

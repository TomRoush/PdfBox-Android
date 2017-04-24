package com.tom_roush.pdfbox.pdmodel.graphics.shading;

import com.tom_roush.pdfbox.cos.COSDictionary;

/**
 * Resources for a shading type 7 (Tensor-Product Patch Mesh).
 */
public class PDShadingType7 extends PDShadingType6
{
    /**
     * Constructor using the given shading dictionary.
     *
     * @param shadingDictionary the dictionary for this shading
     */
    public PDShadingType7(COSDictionary shadingDictionary)
    {
        super(shadingDictionary);
    }

    @Override
    public int getShadingType()
    {
        return PDShading.SHADING_TYPE7;
    }

//    @Override
//    public Paint toPaint(Matrix matrix)
//    {
//        return new Type7ShadingPaint(this, matrix);
//    }TODO: PdfBox-Android
}

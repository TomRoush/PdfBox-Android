package org.apache.pdfbox.pdmodel.graphics.shading;

import org.apache.pdfbox.cos.COSDictionary;

/**
 * Resources for a shading type 6 (Coons Patch Mesh).
 */
public class PDShadingType6 extends PDShadingType4
{
    /**
     * Constructor using the given shading dictionary.
     *
     * @param shadingDictionary the dictionary for this shading
     */
    public PDShadingType6(COSDictionary shadingDictionary)
    {
        super(shadingDictionary);
    }

    @Override
    public int getShadingType()
    {
        return PDShading.SHADING_TYPE6;
    }

//    @Override
//    public Paint toPaint(Matrix matrix)
//    {
//        return new Type6ShadingPaint(this, matrix);
//    }TODO
}

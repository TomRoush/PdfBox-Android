package com.tom_roush.pdfbox.pdmodel.graphics.shading;

import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSName;

/**
 * Resources for a shading type 5 (Lattice-Form Gouraud-Shade Triangle Mesh).
 */
public class PDShadingType5 extends PDTriangleBasedShadingType
{
    /**
     * Constructor using the given shading dictionary.
     *
     * @param shadingDictionary the dictionary for this shading
     */
    public PDShadingType5(COSDictionary shadingDictionary)
    {
        super(shadingDictionary);
    }

    @Override
    public int getShadingType()
    {
        return PDShading.SHADING_TYPE5;
    }

    /**
     * The vertices per row of this shading. This will return -1 if one has not
     * been set.
     *
     * @return the number of vertices per row
     */
    public int getVerticesPerRow()
    {
        return getCOSObject().getInt(COSName.VERTICES_PER_ROW, -1);
    }

    /**
     * Set the number of vertices per row.
     *
     * @param verticesPerRow the number of vertices per row
     */
    public void setVerticesPerRow(int verticesPerRow)
    {
        getCOSObject().setInt(COSName.VERTICES_PER_ROW, verticesPerRow);
    }

//    @Override
//    public Paint toPaint(Matrix matrix)
//    {
//        return new Type5ShadingPaint(this, matrix);
//    }TODO: PdfBox-Android
}

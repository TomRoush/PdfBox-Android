package org.apache.pdfbox.pdmodel.graphics.shading;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.util.Matrix;

/**
 * Resources for a function based shading.
 */
public class PDShadingType1 extends PDShading
{
    private COSArray domain = null;

    /**
     * Constructor using the given shading dictionary.
     *
     * @param shadingDictionary the dictionary for this shading
     */
    public PDShadingType1(COSDictionary shadingDictionary)
    {
        super(shadingDictionary);
    }

    @Override
    public int getShadingType()
    {
        return PDShading.SHADING_TYPE1;
    }

    /**
     * This will get the optional Matrix of a function based shading.
     *
     * @return the matrix
     */
    public Matrix getMatrix()
    {
        Matrix matrix = null;
        COSArray array = (COSArray) getCOSDictionary().getDictionaryObject(COSName.MATRIX);
        if (array != null)
        {
            matrix = new Matrix();
            matrix.setValue(0, 0, ((COSNumber) array.get(0)).floatValue());
            matrix.setValue(0, 1, ((COSNumber) array.get(1)).floatValue());
            matrix.setValue(1, 0, ((COSNumber) array.get(2)).floatValue());
            matrix.setValue(1, 1, ((COSNumber) array.get(3)).floatValue());
            matrix.setValue(2, 0, ((COSNumber) array.get(4)).floatValue());
            matrix.setValue(2, 1, ((COSNumber) array.get(5)).floatValue());
        }
        return matrix;
    }

    /**
     * Sets the optional Matrix entry for the function based shading.
     *
     * @param transform the transformation matrix
     */
    public void setMatrix(android.graphics.Matrix transform)
    {
        COSArray matrix = new COSArray();
        float[] values = new float[9];
        transform.getValues(values);
        for (float v : values)
        {
            matrix.add(new COSFloat((float) v));
        }
        getCOSDictionary().setItem(COSName.MATRIX, matrix);
    }

    /**
     * This will get the optional Domain values of a function based shading.
     *
     * @return the domain values
     */
    public COSArray getDomain()
    {
        if (domain == null)
        {
            domain = (COSArray) getCOSDictionary().getDictionaryObject(COSName.DOMAIN);
        }
        return domain;
    }

    /**
     * Sets the optional Domain entry for the function based shading.
     *
     * @param newDomain the domain array
     */
    public void setDomain(COSArray newDomain)
    {
        domain = newDomain;
        getCOSDictionary().setItem(COSName.DOMAIN, newDomain);
    }

//    @Override
//    public Paint toPaint(Matrix matrix)
//    {
//        return new Type1ShadingPaint(this, matrix);
//    }TODO
}

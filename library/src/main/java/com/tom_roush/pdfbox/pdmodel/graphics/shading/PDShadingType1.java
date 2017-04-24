package com.tom_roush.pdfbox.pdmodel.graphics.shading;

import com.tom_roush.pdfbox.cos.COSArray;
import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSFloat;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.util.Matrix;

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
        COSArray array = (COSArray) getCOSObject().getDictionaryObject(COSName.MATRIX);
        if (array != null)
    	{
    		return new Matrix(array);
    	}
    	else
    	{
    		// identity matrix is the default
    		return new Matrix();
    	}
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
            matrix.add(new COSFloat(v));
        }
        getCOSObject().setItem(COSName.MATRIX, matrix);
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
            domain = (COSArray) getCOSObject().getDictionaryObject(COSName.DOMAIN);
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
        getCOSObject().setItem(COSName.DOMAIN, newDomain);
    }

//    @Override
//    public Paint toPaint(Matrix matrix)
//    {
//        return new Type1ShadingPaint(this, matrix);
//    }TODO: PdfBox-Android
}

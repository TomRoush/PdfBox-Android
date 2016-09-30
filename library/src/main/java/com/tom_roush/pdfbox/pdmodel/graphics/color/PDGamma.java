package com.tom_roush.pdfbox.pdmodel.graphics.color;

import com.tom_roush.pdfbox.cos.COSArray;
import com.tom_roush.pdfbox.cos.COSBase;
import com.tom_roush.pdfbox.cos.COSFloat;
import com.tom_roush.pdfbox.cos.COSNumber;

import com.tom_roush.pdfbox.pdmodel.common.COSObjectable;

/**
 * A gamma array, or collection of three floating point parameters used for color operations.
 *
 * @author Ben Litchfield
 */
public final class PDGamma implements COSObjectable
{
    private COSArray values = null;

    /**
     * Creates a new gamma.
     * Defaults all values to 0, 0, 0.
     */
    public PDGamma()
    {
        values = new COSArray();
        values.add(new COSFloat(0.0f));
        values.add(new COSFloat(0.0f));
        values.add(new COSFloat(0.0f));
    }

    /**
     * Creates a new gamma from a COS array.
     * @param array the array containing the XYZ values
     */
    public PDGamma(COSArray array)
    {
        values = array;
    }

    /**
     * Convert this standard java object to a COS object.
     * @return the cos object that matches this Java object
     */
    public COSBase getCOSObject()
    {
        return values;
    }

    /**
     * Convert this standard java object to a COS object.
     * @return the cos object that matches this Java object
     */
    public COSArray getCOSArray()
    {
        return values;
    }

    /**
     * Returns the r value of the tristimulus.
     * @return the R value.
     */
    public float getR()
    {
        return ((COSNumber)values.get(0)).floatValue();
    }

    /**
     * Sets the r value of the tristimulus.
     * @param r the r value for the tristimulus
     */
    public void setR(float r)
    {
        values.set(0, new COSFloat(r));
    }

    /**
     * Returns the g value of the tristimulus.
     * @return the g value
     */
    public float getG()
    {
        return ((COSNumber)values.get(1)).floatValue();
    }

    /**
     * Sets the g value of the tristimulus.
     * @param g the g value for the tristimulus
     */
    public void setG(float g)
    {
        values.set(1, new COSFloat(g));
    }

    /**
     * Returns the b value of the tristimulus.
     * @return the B value
     */
    public float getB()
    {
        return ((COSNumber)values.get(2)).floatValue();
    }

    /**
     * Sets the b value of the tristimulus.
     * @param b he b value for the tristimulus
     */
    public void setB(float b)
    {
        values.set(2, new COSFloat(b));
    }
}

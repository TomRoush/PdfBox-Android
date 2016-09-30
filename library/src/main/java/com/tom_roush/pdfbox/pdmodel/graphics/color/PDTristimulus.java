package com.tom_roush.pdfbox.pdmodel.graphics.color;

import com.tom_roush.pdfbox.cos.COSArray;
import com.tom_roush.pdfbox.cos.COSBase;
import com.tom_roush.pdfbox.cos.COSFloat;
import com.tom_roush.pdfbox.cos.COSNumber;

import com.tom_roush.pdfbox.pdmodel.common.COSObjectable;

/**
 * A tristimulus, or collection of three floating point parameters used for color operations.
 *
 * @author Ben Litchfield
 */
public final class PDTristimulus implements COSObjectable
{
    private COSArray values = null;

    /**
     * Constructor. Defaults all values to 0, 0, 0.
     */
    public PDTristimulus()
    {
        values = new COSArray();
        values.add(new COSFloat(0.0f));
        values.add(new COSFloat(0.0f));
        values.add(new COSFloat(0.0f));
    }

    /**
     * Constructor from COS object.
     * @param array the array containing the XYZ values
     */
    public PDTristimulus(COSArray array)
    {
        values = array;
    }

    /**
     * Constructor from COS object.
     * @param array the array containing the XYZ values
     */
    public PDTristimulus(float[] array)
    {
        values = new COSArray();
        for(int i=0; i<array.length && i<3; i++)
        {
            values.add(new COSFloat(array[i]));
        }
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
     * Returns the x value of the tristimulus.
     * @return the X value
     */
    public float getX()
    {
        return ((COSNumber)values.get(0)).floatValue();
    }

    /**
     * Sets the x value of the tristimulus.
     * @param x the x value for the tristimulus
     */
    public void setX(float x)
    {
        values.set(0, new COSFloat(x));
    }

    /**
     * Returns the y value of the tristimulus.
     * @return the Y value
     */
    public float getY()
    {
        return ((COSNumber)values.get(1)).floatValue();
    }

    /**
     * Sets the y value of the tristimulus.
     * @param y the y value for the tristimulus
     */
    public void setY(float y)
    {
        values.set(1, new COSFloat(y));
    }

    /**
     * Returns the z value of the tristimulus.
     * @return the Z value
     */
    public float getZ()
    {
        return ((COSNumber)values.get(2)).floatValue();
    }

    /**
     * Sets the z value of the tristimulus.
     * @param z the z value for the tristimulus
     */
    public void setZ(float z)
    {
        values.set(2, new COSFloat(z));
    }
}

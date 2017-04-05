package com.tom_roush.pdfbox.pdmodel.graphics.shading;

import com.tom_roush.pdfbox.cos.COSArray;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.pdmodel.common.PDRange;
import com.tom_roush.pdfbox.cos.COSDictionary;

/**
 * Intermediate class extended by the shading types 4,5,6 and 7 that contains the common methods
 * used by those classes
 */
abstract class PDTriangleBasedShadingType extends PDShading
{
    // an array of 2^n numbers specifying the linear mapping of sample values
    // into the range appropriate for the function's output values. Default
    // value: same as the value of Range
    private COSArray decode = null;

    PDTriangleBasedShadingType(COSDictionary shadingDictionary)
    {
        super(shadingDictionary);
    }

    /**
     * The bits per component of this shading. This will return -1 if one has
     * not been set.
     *
     * @return the number of bits per component
     */
    public int getBitsPerComponent()
    {
        return getCOSObject().getInt(COSName.BITS_PER_COMPONENT, -1);
    }

    /**
     * Set the number of bits per component.
     *
     * @param bitsPerComponent the number of bits per component
     */
    public void setBitsPerComponent(int bitsPerComponent)
    {
        getCOSObject().setInt(COSName.BITS_PER_COMPONENT, bitsPerComponent);
    }

    /**
     * The bits per coordinate of this shading. This will return -1 if one has
     * not been set.
     *
     * @return the number of bits per coordinate
     */
    public int getBitsPerCoordinate()
    {
        return getCOSObject().getInt(COSName.BITS_PER_COORDINATE, -1);
    }

    /**
     * Set the number of bits per coordinate.
     *
     * @param bitsPerComponent the number of bits per coordinate
     */
    public void setBitsPerCoordinate(int bitsPerComponent)
    {
        getCOSObject().setInt(COSName.BITS_PER_COORDINATE, bitsPerComponent);
    }

    /**
     * Returns all decode values as COSArray.
     *
     * @return the decode array
     */
    private COSArray getDecodeValues()
    {
        if (decode == null)
        {
            decode = (COSArray) getCOSObject().getDictionaryObject(COSName.DECODE);
        }
        return decode;
    }

    /**
     * This will set the decode values.
     *
     * @param decodeValues the new decode values
     */
    public void setDecodeValues(COSArray decodeValues)
    {
        decode = decodeValues;
        getCOSObject().setItem(COSName.DECODE, decodeValues);
    }

    /**
     * Get the decode for the input parameter.
     *
     * @param paramNum the function parameter number
     * @return the decode parameter range or null if none is set
     */
    public PDRange getDecodeForParameter(int paramNum)
    {
        PDRange retval = null;
        COSArray decodeValues = getDecodeValues();
        if (decodeValues != null && decodeValues.size() >= paramNum * 2 + 1)
        {
            retval = new PDRange(decodeValues, paramNum);
        }
        return retval;
    }

}

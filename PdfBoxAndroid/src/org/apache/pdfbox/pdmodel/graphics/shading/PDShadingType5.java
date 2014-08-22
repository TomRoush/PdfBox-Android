package org.apache.pdfbox.pdmodel.graphics.shading;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.PDRange;

/**
 * This represents resources for a shading type 5 (Lattice-Form Gouraud-Shaded
 * Triangle Meshes).
 *
 */
public class PDShadingType5 extends PDShadingResources
{

    /**
     * An array of 2^n numbers specifying the linear mapping of sample values
     * into the range appropriate for the function's output values. Default
     * value: same as the value of Range
     */
    private COSArray decode = null;

    /**
     * Constructor using the given shading dictionary.
     *
     * @param shadingDictionary The dictionary for this shading.
     */
    public PDShadingType5(COSDictionary shadingDictionary)
    {
        super(shadingDictionary);
    }

    /**
     * {@inheritDoc}
     */
    public int getShadingType()
    {
        return PDShadingResources.SHADING_TYPE5;
    }

    /**
     * The bits per component of this shading. This will return -1 if one has
     * not been set.
     *
     * @return The number of bits per component.
     */
    public int getBitsPerComponent()
    {
        return getCOSDictionary().getInt(COSName.BITS_PER_COMPONENT, -1);
    }

    /**
     * Set the number of bits per component.
     *
     * @param bpc The number of bits per component.
     */
    public void setBitsPerComponent(int bpc)
    {
        getCOSDictionary().setInt(COSName.BITS_PER_COMPONENT, bpc);
    }

    /**
     * The bits per coordinate of this shading. This will return -1 if one has
     * not been set.
     *
     * @return The number of bits per coordinate.
     */
    public int getBitsPerCoordinate()
    {
        return getCOSDictionary().getInt(COSName.BITS_PER_COORDINATE, -1);
    }

    /**
     * Set the number of bits per coordinate.
     *
     * @param bpc The number of bits per coordinate.
     */
    public void setBitsPerCoordinate(int bpc)
    {
        getCOSDictionary().setInt(COSName.BITS_PER_COORDINATE, bpc);
    }

    /**
     * The vertices per row of this shading. This will return -1 if one has not
     * been set.
     *
     * @return The number of vertices per row.
     */
    public int getVerticesPerRow()
    {
        return getCOSDictionary().getInt(COSName.VERTICES_PER_ROW, -1);
    }

    /**
     * Set the number of vertices per row.
     *
     * @param vpr The number of vertices per row.
     */
    public void setVerticesPerRow(int vpr)
    {
        getCOSDictionary().setInt(COSName.VERTICES_PER_ROW, vpr);
    }

    /**
     * Returns all decode values as COSArray.
     *
     * @return the decode array.
     */
    private COSArray getDecodeValues()
    {
        if (decode == null)
        {
            decode = (COSArray) getCOSDictionary().getDictionaryObject(COSName.DECODE);
        }
        return decode;
    }

    /**
     * This will set the decode values.
     *
     * @param decodeValues The new decode values.
     */
    public void setDecodeValues(COSArray decodeValues)
    {
        decode = decodeValues;
        getCOSDictionary().setItem(COSName.DECODE, decodeValues);
    }

    /**
     * Get the decode for the input parameter.
     *
     * @param paramNum The function parameter number.
     *
     * @return The decode parameter range or null if none is set.
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

package com.tom_roush.pdfbox.pdmodel.documentinterchange.prepress;

import com.tom_roush.pdfbox.cos.COSArray;
import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSInteger;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.pdmodel.common.COSObjectable;
import com.tom_roush.pdfbox.pdmodel.graphics.PDLineDashPattern;
import com.tom_roush.pdfbox.pdmodel.graphics.color.PDColor;
import com.tom_roush.pdfbox.pdmodel.graphics.color.PDDeviceRGB;

/**
 * The Box Style specifies visual characteristics for displaying box areas.
 *
 * @author Ben Litchfield
 */
public final class PDBoxStyle implements COSObjectable
{
    /**
     * Style for guideline.
     */
    public static final String GUIDELINE_STYLE_SOLID = "S";
    /**
     * Style for guideline.
     */
    public static final String GUIDELINE_STYLE_DASHED = "D";

    private COSDictionary dictionary;

    /**
     * Default Constructor.
     *
     */
    public PDBoxStyle()
    {
        dictionary = new COSDictionary();
    }

    /**
     * Constructor for an existing BoxStyle element.
     *
     * @param dic The existing dictionary.
     */
    public PDBoxStyle( COSDictionary dic )
    {
        dictionary = dic;
    }

    /**
     * Convert this standard java object to a COS object.
     *
     * @return The cos object that matches this Java object.
     */
    @Override
    public COSDictionary getCOSObject()
    {
        return dictionary;
    }

    /**
     * Get the RGB color to be used for the guidelines.  This is guaranteed to
     * not return null. The default color is [0,0,0].
     *
     *@return The guideline color.
     */
    public PDColor getGuidelineColor()
    {
        COSArray colorValues = (COSArray) dictionary.getDictionaryObject(COSName.C);
        if( colorValues == null )
        {
            colorValues = new COSArray();
            colorValues.add( COSInteger.ZERO );
            colorValues.add( COSInteger.ZERO );
            colorValues.add( COSInteger.ZERO );
            dictionary.setItem( "C", colorValues );
        }
        PDColor color = new PDColor(colorValues.toFloatArray(), PDDeviceRGB.INSTANCE);
        return color;
    }

    /**
     * Set the color space instance for this box style.  This must be a
     * PDDeviceRGB!
     *
     * @param color The new colorspace value.
     */
    public void setGuideLineColor( PDColor color )
    {
        COSArray values = null;
        if( color != null )
        {
            values = color.toCOSArray();
        }
        dictionary.setItem(COSName.C, values);
    }

    /**
     * Get the width of the of the guideline in default user space units.
     * The default is 1.
     *
     * @return The width of the guideline.
     */
    public float getGuidelineWidth()
    {
        return dictionary.getFloat(COSName.W, 1);
    }

    /**
     * Set the guideline width.
     *
     * @param width The width in default user space units.
     */
    public void setGuidelineWidth( float width )
    {
        dictionary.setFloat(COSName.W, width);
    }

    /**
     * Get the style for the guideline.  The default is "S" for solid.
     *
     * @return The guideline style.
     * @see PDBoxStyle#GUIDELINE_STYLE_DASHED
     * @see PDBoxStyle#GUIDELINE_STYLE_SOLID
     */
    public String getGuidelineStyle()
    {
        return dictionary.getNameAsString(COSName.S, GUIDELINE_STYLE_SOLID);
    }

    /**
     * Set the style for the box.
     *
     * @param style The style for the box line.
     * @see PDBoxStyle#GUIDELINE_STYLE_DASHED
     * @see PDBoxStyle#GUIDELINE_STYLE_SOLID
     */
    public void setGuidelineStyle( String style )
    {
        dictionary.setName(COSName.S, style);
    }

    /**
     * Get the line dash pattern for this box style.  This is guaranteed to not
     * return null.  The default is [3],0.
     *
     * @return The line dash pattern.
     */
    public PDLineDashPattern getLineDashPattern()
    {
        PDLineDashPattern pattern;
        COSArray d = (COSArray) dictionary.getDictionaryObject(COSName.D);
        if( d == null )
        {
            d = new COSArray();
            d.add( COSInteger.THREE );
            dictionary.setItem(COSName.D, d);
        }
        COSArray lineArray = new COSArray();
        lineArray.add( d );
        //dash phase is not specified and assumed to be zero.
        pattern = new PDLineDashPattern( lineArray, 0 );
        return pattern;
    }

    /**
     * Set the line dash pattern associated with this box style.
     *
     * @param dashArray The patter for this box style.
     */
    public void setLineDashPattern( COSArray dashArray )
    {
        COSArray array = null;
        if( dashArray != null )
        {
            array = dashArray;
        }
        dictionary.setItem(COSName.D, array);
    }
}

package com.tom_roush.pdfbox.pdmodel.graphics.pattern;

import com.tom_roush.pdfbox.contentstream.PDContentStream;
import com.tom_roush.pdfbox.cos.COSArray;
import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.cos.COSStream;
import com.tom_roush.pdfbox.pdmodel.PDResources;
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle;
import com.tom_roush.pdfbox.pdmodel.common.PDStream;

import java.io.IOException;
import java.io.InputStream;

/**
 * A tiling pattern dictionary.
 */
public class PDTilingPattern extends PDAbstractPattern implements PDContentStream
{
	/** paint type 1 = colored tiling pattern. */
	public static final int PAINT_COLORED = 1;

	/** paint type 2 = uncolored tiling pattern. */
	public static final int PAINT_UNCOLORED = 2;

	/** tiling type 1 = constant spacing.*/
	public static final int TILING_CONSTANT_SPACING = 1;

	/**  tiling type 2 = no distortion. */
	public static final int TILING_NO_DISTORTION = 2;

	/** tiling type 3 = constant spacing and faster tiling. */
	public static final int TILING_CONSTANT_SPACING_FASTER_TILING = 3;

	/**
	 * Creates a new tiling pattern.
	 */
	public PDTilingPattern()
	{
		super();
		getCOSObject().setInt(COSName.PATTERN_TYPE, PDAbstractPattern.TYPE_TILING_PATTERN);
	}

	/**
	 * Creates a new tiling pattern from the given COS dictionary.
	 * @param resourceDictionary The COSDictionary for this pattern resource.
	 */
	public PDTilingPattern(COSDictionary resourceDictionary)
	{
		super(resourceDictionary);
	}

	@Override
	public int getPatternType()
	{
		return PDAbstractPattern.TYPE_TILING_PATTERN;
	}

	/**
	 * This will set the paint type.
	 * @param paintType The new paint type.
	 */
	@Override
	public void setPaintType(int paintType)
	{
		getCOSObject().setInt(COSName.PAINT_TYPE, paintType);
	}

	/**
	 * This will return the paint type.
	 * @return The paint type
	 */
	public int getPaintType()
	{
		return getCOSObject().getInt(COSName.PAINT_TYPE, 0);
	}

	/**
	 * This will set the tiling type.
	 * @param tilingType The new tiling type.
	 */
	public void setTilingType(int tilingType)
	{
		getCOSObject().setInt(COSName.TILING_TYPE, tilingType);
	}

	/**
	 * This will return the tiling type.
	 * @return The tiling type
	 */
	public int getTilingType()
	{
		return getCOSObject().getInt(COSName.TILING_TYPE, 0);
	}

	/**
	 * This will set the XStep value.
	 * @param xStep The new XStep value.
	 */
	public void setXStep(float xStep)
	{
		getCOSObject().setFloat(COSName.X_STEP, xStep);
	}

	/**
	 * This will return the XStep value.
	 * @return The XStep value
	 */
	public float getXStep()
	{
		// ignores invalid values, see PDFBOX-1094-065514-XStep32767.pdf
		float xStep = getCOSObject().getFloat(COSName.X_STEP, 0);
		return xStep == Short.MAX_VALUE ? 0 : xStep;
	}

	/**
	 * This will set the YStep value.
	 * @param yStep The new YStep value.
	 */
	public void setYStep(float yStep)
	{
		getCOSObject().setFloat(COSName.Y_STEP, yStep);
	}

	/**
	 * This will return the YStep value.
	 * @return The YStep value
	 */
	public float getYStep()
	{
		// ignores invalid values, see PDFBOX-1094-065514-XStep32767.pdf
		float yStep = getCOSObject().getFloat(COSName.Y_STEP, 0);
		return yStep == Short.MAX_VALUE ? 0 : yStep;
	}

    public PDStream getContentStream()
    {
        return new PDStream((COSStream) getCOSObject());
    }

	@Override
    public InputStream getContents() throws IOException
    {
        return ((COSStream) getCOSObject()).getUnfilteredStream();
    }

	/**
	 * This will get the resources for this pattern.
	 * This will return null if no resources are available at this level.
	 * @return The resources for this pattern.
	 */
	@Override
	public PDResources getResources()
	{
		PDResources retval = null;
		COSDictionary resources = (COSDictionary) getCOSObject().getDictionaryObject(COSName.RESOURCES);
		if( resources != null )
		{
			retval = new PDResources( resources );
		}
		return retval;
	}

	/**
	 * This will set the resources for this pattern.
	 * @param resources The new resources for this pattern.
	 */
	public void setResources( PDResources resources )
	{
		getCOSObject().setItem(COSName.RESOURCES, resources);
	}

	/**
	 * An array of four numbers in the form coordinate system (see
	 * below), giving the coordinates of the left, bottom, right, and top edges,
	 * respectively, of the pattern's bounding box.
	 *
	 * @return The BBox of the pattern.
	 */
	@Override
	public PDRectangle getBBox()
	{
		PDRectangle retval = null;
		COSArray array = (COSArray) getCOSObject().getDictionaryObject(COSName.BBOX);
		if( array != null )
		{
			retval = new PDRectangle( array );
		}
		return retval;
	}

	/**
	 * This will set the BBox (bounding box) for this Pattern.
	 * @param bbox The new BBox for this Pattern.
	 */
	public void setBBox(PDRectangle bbox)
	{
		if( bbox == null )
		{
			getCOSObject().removeItem(COSName.BBOX);
		}
		else
		{
			getCOSObject().setItem(COSName.BBOX, bbox.getCOSArray());
		}
	}
}
package com.tom_roush.pdfbox.pdmodel.graphics.color;

import com.tom_roush.pdfbox.cos.COSBase;
import com.tom_roush.pdfbox.cos.COSName;

/**
 * Device colour spaces directly specify colours or shades of gray produced by an output device.
 *
 * @author John Hewson
 */
public abstract class PDDeviceColorSpace extends PDColorSpace
{
	@Override
	public String toString()
	{
		return getName();
	}

	@Override
	public COSBase getCOSObject()
	{
		return COSName.getPDFName(getName());
	}
}
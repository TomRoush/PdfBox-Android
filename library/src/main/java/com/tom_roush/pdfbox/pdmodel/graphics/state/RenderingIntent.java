package com.tom_roush.pdfbox.pdmodel.graphics.state;

/**
 * Rendering intent.
 *
 * @author John Hewson
 */
public enum RenderingIntent
{
	/**
	 * Absolute Colorimetric.
	 */
	ABSOLUTE_COLORIMETRIC("AbsoluteColorimetric"),

	/**
	 * Relative Colorimetric.
	 */
	RELATIVE_COLORIMETRIC("RelativeColorimetric"),

	/**
	 * Saturation.
	 */
	SATURATION("Saturation"),

	/**
	 * Perceptual
	 */
	PERCEPTUAL("Perceptual");

	public static RenderingIntent fromString(String value)
	{
		if (value.equals("AbsoluteColorimetric"))
		{
			return ABSOLUTE_COLORIMETRIC;
		}
		else if (value.equals("RelativeColorimetric"))
		{
			return RELATIVE_COLORIMETRIC;
		}
		else if (value.equals("Saturation"))
		{
			return SATURATION;
		}
		else if (value.equals("Perceptual"))
		{
			return PERCEPTUAL;
		}
		throw new IllegalArgumentException(value);
	}

	private final String value;

	RenderingIntent(String value)
	{
		this.value = value;
	}

	/**
	 * Returns the string value, as used in a PDF file.
	 */
	public String stringValue()
	{
		return value;
	}
}
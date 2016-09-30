package com.tom_roush.pdfbox.filter;

import java.io.IOException;

/**
 * Thrown when a required JAI ImageReader is missing.
 *
 * @author John Hewson
 */
public class MissingImageReaderException extends IOException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    public MissingImageReaderException(String message)
    {
        super(message);
    }
}

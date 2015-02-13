package org.apache.pdfbox.filter;

import java.io.IOException;

/**
 * Thrown when a required JAI ImageReader is missing.
 *
 * @author John Hewson
 */
public class MissingImageReaderException extends IOException
{
    public MissingImageReaderException(String message)
    {
        super(message);
    }
}

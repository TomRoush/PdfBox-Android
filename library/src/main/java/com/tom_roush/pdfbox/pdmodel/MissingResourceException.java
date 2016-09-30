package org.apache.pdfbox.pdmodel;

import java.io.IOException;

/**
 * Thrown when a named resource is missing.
 */
public final class MissingResourceException extends IOException
{
    public MissingResourceException(String message)
    {
        super(message);
    }
}
package com.tom_roush.fontbox.type1;

import java.io.IOException;

/**
 * Thrown when a font is damaged and cannot be read.
 *
 * @author John Hewson
 */
public class DamagedFontException extends IOException
{
    public DamagedFontException(String message)
    {
        super(message);
    }
}

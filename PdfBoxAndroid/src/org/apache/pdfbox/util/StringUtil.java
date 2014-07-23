package org.apache.pdfbox.util;

import java.io.UnsupportedEncodingException;

public class StringUtil
{
    /**
     * Converts a string to it ISO-8859-1 byte sequence
     *
     * It is an workaround for variable initialisations outside of functions.
     */ 
    public static byte[] getBytes(String s)
    {
        try
            {
                return s.getBytes("ISO-8859-1");
            }
        catch(UnsupportedEncodingException e)
            {
                throw new RuntimeException("Unsupported Encoding", e);
            }
    }
}

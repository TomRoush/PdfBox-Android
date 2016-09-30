package com.tom_roush.pdfbox.util;

/**
 * Utility functions for hex encoding.
 *
 * @author John Hewson
 */
public final class Hex
{
    private Hex() {}

    /**
     * Returns a hex string of the given byte.
     */
    public static String getString(byte b)
    {
        return Integer.toHexString(0x100 | b & 0xff).substring(1).toUpperCase();
    }

    /**
     * Returns the bytes corresponding to the ASCII hex encoding of the given byte.
     */
    public static byte[] getBytes(byte b)
    {
        return getString(b).getBytes(Charsets.US_ASCII);
    }
}
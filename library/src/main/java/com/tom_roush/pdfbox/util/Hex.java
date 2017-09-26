package com.tom_roush.pdfbox.util;

/**
 * Utility functions for hex encoding.
 *
 * @author John Hewson
 */
public final class Hex
{
    /**
     * for hex conversion.
     *
     * https://stackoverflow.com/questions/2817752/java-code-to-convert-byte-to-hexadecimal
     */
    private static final byte[] HEX_BYTES = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
        'B', 'C', 'D', 'E', 'F'};
    private static final char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
        'B', 'C', 'D', 'E', 'F'};

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

    /**
     * Returns the bytes corresponding to the ASCII hex encoding of the given bytes.
     */
    public static byte[] getBytes(byte[] bytes)
    {
        byte[] asciiBytes = new byte[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++)
        {
            asciiBytes[i * 2] = HEX_BYTES[getHighNibble(bytes[i])];
            asciiBytes[i * 2 + 1] = HEX_BYTES[getLowNibble(bytes[i])];
        }
        return asciiBytes;
    }

    /**
     * Get the high nibble of the given byte.
     *
     * @param b the given byte
     * @return the high nibble
     */
    private static int getHighNibble(byte b)
    {
        return (b & 0xF0) >> 4;
    }

    /**
     * Get the low nibble of the given byte.
     *
     * @param b the given byte
     * @return the low nibble
     */
    private static int getLowNibble(byte b)
    {
        return b & 0x0F;
    }
}
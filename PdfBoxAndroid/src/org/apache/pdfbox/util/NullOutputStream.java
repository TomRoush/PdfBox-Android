package org.apache.pdfbox.util;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Dummy output stream, everything written there gets lost.
 *
 * @author Tilman Hausherr
 */
public class NullOutputStream extends OutputStream
{
    @Override
    public void write(int b) throws IOException
    {
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException
    {
    }

    @Override
    public void write(byte[] b) throws IOException
    {
    }
}

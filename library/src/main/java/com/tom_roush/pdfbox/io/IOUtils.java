package org.apache.pdfbox.io;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class contains various I/O-related methods.
 */
public final class IOUtils
{

    //TODO PDFBox should really use Apache Commons IO.

    private IOUtils()
    {
        //Utility class. Don't instantiate.
    }

    /**
     * Reads the input stream and returns its contents as a byte array.
     * @param in the input stream to read from.
     * @return the byte array
     * @throws IOException if an I/O error occurs
     */
    public static byte[] toByteArray(InputStream in) throws IOException
    {
        ByteArrayOutputStream baout = new ByteArrayOutputStream();
        copy(in, baout);
        return baout.toByteArray();
    }

    /**
     * Copies all the contents from the given input stream to the given output stream.
     * @param input the input stream
     * @param output the output stream
     * @return the number of bytes that have been copied
     * @throws IOException if an I/O error occurs
     */
    public static long copy(InputStream input, OutputStream output) throws IOException
    {
        byte[] buffer = new byte[4096];
        long count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer)))
        {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    /**
     * Populates the given buffer with data read from the input stream. If the data doesn't
     * fit the buffer, only the data that fits in the buffer is read. If the data is less than
     * fits in the buffer, the buffer is not completely filled.
     * @param in the input stream to read from
     * @param buffer the buffer to fill
     * @return the number of bytes written to the buffer
     * @throws IOException if an I/O error occurs
     */
    public static long populateBuffer(InputStream in, byte[] buffer) throws IOException
    {
        int remaining = buffer.length;
        while (remaining > 0)
        {
            int bufferWritePos = buffer.length - remaining;
            int bytesRead = in.read(buffer, bufferWritePos, remaining);
            if (bytesRead < 0)
            {
                break; //EOD
            }
            remaining -= bytesRead;
        }
        return buffer.length - remaining;
    }
    
    /**
     * Null safe close of the given {@link Closeable} suppressing any exception.
     *
     * @param closeable to be closed
     */
    public static void closeQuietly(Closeable closeable)
    {
    	try
    	{
    		if (closeable != null)
    		{
    			closeable.close();
    		}
    	}
    	catch (IOException ioe)
    	{
    		// ignore
    	}
    }
}

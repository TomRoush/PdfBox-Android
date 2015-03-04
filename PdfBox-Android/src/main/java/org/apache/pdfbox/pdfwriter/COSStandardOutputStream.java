package org.apache.pdfbox.pdfwriter;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * simple output stream with some minor features for generating "pretty" PDF files.
 *
 * @author Michael Traut
 */
public class COSStandardOutputStream extends FilterOutputStream
{
    /**
     * To be used when 2 byte sequence is enforced.
     */
    public static final byte[] CRLF = { '\r', '\n' };

    /**
     * Line feed character.
     */
    public static final byte[] LF = { '\n' };

    /**
     * standard line separator.
     */
    public static final byte[] EOL = { '\n' };

    // current byte position in the output stream
    private long position = 0;

    // flag to prevent generating two newlines in sequence
    private boolean onNewLine = false;
    
    /**
     * COSOutputStream constructor comment.
     *
     * @param out The underlying stream to write to.
     */
    public COSStandardOutputStream(OutputStream out)
    {
        super(out);
    }

    /**
     * COSOutputStream constructor comment.
     *
     * @param out The underlying stream to write to.
     * @param position The current position of output stream.
     */
    public COSStandardOutputStream(OutputStream out, int position)
    {
        super(out);
        this.position = position;
    }
    
    /**
     * This will get the current position in the stream.
     *
     * @return The current position in the stream.
     */
    public long getPos()
    {
        return position;
    }
    
    /**
     * This will tell if we are on a newline.
     *
     * @return true If we are on a newline.
     */
    public boolean isOnNewLine()
    {
        return onNewLine;
    }
    /**
     * This will set a flag telling if we are on a newline.
     *
     * @param newOnNewLine The new value for the onNewLine attribute.
     */
    public void setOnNewLine(boolean newOnNewLine)
    {
        onNewLine = newOnNewLine;
    }

    /**
     * This will write some byte to the stream.
     *
     * @param b The source byte array.
     * @param off The offset into the array to start writing.
     * @param len The number of bytes to write.
     *
     * @throws IOException If the underlying stream throws an exception.
     */
    @Override
    public void write(byte[] b, int off, int len) throws IOException
    {
        setOnNewLine(false);
        out.write(b, off, len);
        position += len;
    }

    /**
     * This will write a single byte to the stream.
     *
     * @param b The byte to write to the stream.
     *
     * @throws IOException If there is an error writing to the underlying stream.
     */
    @Override
    public void write(int b) throws IOException
    {
        setOnNewLine(false);
        out.write(b);
        position++;
    }
    
    /**
     * This will write a CRLF to the stream.
     *
     * @throws IOException If there is an error writing the data to the stream.
     */
    public void writeCRLF() throws IOException
    {
        write(CRLF);
    }

    /**
     * This will write an EOL to the stream.
     *
     * @throws IOException If there is an error writing to the stream
     */
    public void writeEOL() throws IOException
    {
        if (!isOnNewLine())
        {
            write(EOL);
            setOnNewLine(true);
        }
    }

    /**
     * This will write a Linefeed to the stream.
     *
     * @throws IOException If there is an error writing to the underlying stream.
     */
    public void writeLF() throws IOException
    {
        write(LF);
    }
}

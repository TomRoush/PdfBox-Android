package org.apache.pdfboxandroid.io;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

public class PushBackInputStream extends PushbackInputStream {
	/*
     * The current position in the file. 
     */
    private long offset = 0;
	
	/** In case provided input stream implements {@link RandomAccessRead} we hold
     *  a typed reference to it in order to support seek operations. */
    private final RandomAccessRead raInput;
	
	/**
     * Constructor.
     *
     * @param input The input stream.
     * @param size The size of the push back buffer.
     *
     * @throws IOException If there is an error with the stream.
     */
    public PushBackInputStream( InputStream input, int size ) throws IOException
    {
        super( input, size );
        if( input == null )
        {
            throw new IOException( "Error: input was null" );
        }
        
        raInput = ( input instanceof RandomAccessRead ) ?
										(RandomAccessRead) input : null;
    }
    
    /**
     * A simple test to see if we are at the end of the stream.
     *
     * @return true if we are at the end of the stream.
     *
     * @throws IOException If there is an error reading the next byte.
     */
    public boolean isEOF() throws IOException
    {
        int peek = peek();
        return peek == -1;
    }
    
    /**
     * This will peek at the next byte.
     *
     * @return The next byte on the stream, leaving it as available to read.
     *
     * @throws IOException If there is an error reading the next byte.
     */
    public int peek() throws IOException
    {
        int result = read();
        if( result != -1 )
        {
            unread( result );
        }
        return result;
    }
    
    /**
     * Returns the current byte offset in the file.
     * @return the int byte offset
     */
    public long getOffset()
    {
        return offset;
    }
    
    /**
     * Reads a given number of bytes from the underlying stream.
     * @param length the number of bytes to be read
     * @return a byte array containing the bytes just read
     * @throws IOException if an I/O error occurs while reading data
     */
    public byte[] readFully(int length) throws IOException
    {
        byte[] data = new byte[length];
        int pos = 0;
        while (pos < length)
        {
            int amountRead = read( data, pos, length - pos );
            if (amountRead < 0) 
            {
                throw new EOFException("Premature end of file");
            }
            pos += amountRead;
        }
        return data;
    }
}

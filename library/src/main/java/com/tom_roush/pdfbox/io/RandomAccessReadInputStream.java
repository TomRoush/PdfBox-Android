package com.tom_roush.pdfbox.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class allows a section of a RandomAccessRead to be accessed as an
 * input stream.
 *
 * @author Ben Litchfield
 */
public class RandomAccessReadInputStream extends InputStream
{
    private final RandomAccessRead input;
    private long currentPosition;
    private final long endPosition;

    /**
     * Constructor.
     *
     * @param randomAccessRead The file to read the data from.
     * @param startPosition The position in the file that this stream starts.
     * @param length The length of the input stream.
     */
    public RandomAccessReadInputStream(RandomAccessRead randomAccessRead, long startPosition,
        long length)
    {
        input = randomAccessRead;
        currentPosition = startPosition;
        endPosition = currentPosition+length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int available()
    {
        return (int)(endPosition - currentPosition);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close()
    {
        //do nothing because we want to leave the random access file open.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read() throws IOException
    {
        synchronized (input)
        {
            int retval = -1;
            if( currentPosition < endPosition )
            {
                input.seek(currentPosition);
                currentPosition++;
                retval = input.read();
            }
            return retval;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read( byte[] b, int offset, int length ) throws IOException
    {
        //only allow a read of the amount available.
        if( length > available() )
        {
            length = available();
        }
        int amountRead = -1;
        //only read if there are bytes actually available, otherwise
        //return -1 if the EOF has been reached.
        if( available() > 0 )
        {
            synchronized (input)
            {
                input.seek(currentPosition);
                amountRead = input.read(b, offset, length);
            }
        }
        //update the current cursor position.
        if( amountRead > 0 )
        {
            currentPosition += amountRead;
        }
        return amountRead;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long skip( long amountToSkip )
    {
        long amountSkipped = Math.min( amountToSkip, available() );
        currentPosition+= amountSkipped;
        return amountSkipped;
    }
}

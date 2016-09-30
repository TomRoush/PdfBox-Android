package org.apache.pdfbox.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class allows a section of a RandomAccessFile to be accessed as an
 * input stream.
 *
 * @author Ben Litchfield
 */
public class RandomAccessFileInputStream extends InputStream
{
    private final RandomAccess file;
    private long currentPosition;
    private final long endPosition;

    /**
     * Constructor.
     *
     * @param raFile The file to read the data from.
     * @param startPosition The position in the file that this stream starts.
     * @param length The length of the input stream.
     */
    public RandomAccessFileInputStream( RandomAccess raFile, long startPosition, long length )
    {
        file = raFile;
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
        synchronized(file)
        {
            int retval = -1;
            if( currentPosition < endPosition )
            {
                file.seek( currentPosition );
                currentPosition++;
                retval = file.read();
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
            synchronized(file)
            {
                file.seek( currentPosition );
                amountRead = file.read( b, offset, length );
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

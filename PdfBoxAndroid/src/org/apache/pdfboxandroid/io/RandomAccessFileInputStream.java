package org.apache.pdfboxandroid.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class allows a section of a RandomAccessFile to be accessed as an
 * input stream.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.5 $
 */
public class RandomAccessFileInputStream extends InputStream {
	private RandomAccess file;
	private long currentPosition;
    private long endPosition;
    
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
}

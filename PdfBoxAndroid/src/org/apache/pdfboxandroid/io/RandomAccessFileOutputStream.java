package org.apache.pdfboxandroid.io;

/**
 * This will write to a RandomAccessFile in the filesystem and keep track
 * of the position it is writing to and the length of the stream.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.6 $
 */
import java.io.IOException;
import java.io.OutputStream;

import org.apache.pdfboxandroid.cos.COSBase;
import org.apache.pdfboxandroid.cos.COSNumber;
import org.apache.pdfboxandroid.cos.COSObject;

public class RandomAccessFileOutputStream extends OutputStream {
	private RandomAccess file;
    private long position;
    private long lengthWritten = 0;
    private COSBase expectedLength = null;
    
    /**
     * Constructor to create an output stream that will write to the end of a
     * random access file.
     *
     * @param raf The file to write to.
     *
     * @throws IOException If there is a problem accessing the raf.
     */
    public RandomAccessFileOutputStream( RandomAccess raf ) throws IOException
    {
        file = raf;
        //first get the position that we will be writing to
        position = raf.length();
    }
	
	/**
     * {@inheritDoc}
     */
    public void write( int b ) throws IOException
    {
        file.seek( position+lengthWritten );
        lengthWritten++;
        file.write( b );
    }
    
    /**
     * This will set the expected length of this stream.
     *
     * @param value The expected value.
     */
    public void setExpectedLength(COSBase value)
    {
        expectedLength = value;
    }
    
    /**
     * This will get the position in the RAF that the stream was written
     * to.
     *
     * @return The position in the raf where the file can be obtained.
     */
    public long getPosition()
    {
        return position;
    }
    
    /**
     * The number of bytes written to the stream.
     *
     * @return The number of bytes read to the stream.
     */
    public long getLength()
    {
        long length = -1;
        if( expectedLength instanceof COSNumber )
        {
            length = ((COSNumber)expectedLength).intValue();
        }
        else if( expectedLength instanceof COSObject &&
                 ((COSObject)expectedLength).getObject() instanceof COSNumber )
        {
            length = ((COSNumber)((COSObject)expectedLength).getObject()).intValue();
        }
        if( length == -1 )
        {
            length = lengthWritten;
        }
        return length;
    }
    
    /**
     * Get the amount of data that was actually written to the stream, in theory this
     * should be the same as the length specified but in some cases it doesn't match.
     *
     * @return The number of bytes actually written to this stream.
     */
    public long getLengthWritten()
    {
        return lengthWritten;
    }
}

package com.tom_roush.pdfbox.pdmodel.common;

import com.tom_roush.pdfbox.cos.COSArray;
import com.tom_roush.pdfbox.cos.COSBase;
import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.cos.COSStream;
import com.tom_roush.pdfbox.cos.ICOSVisitor;
import com.tom_roush.pdfbox.io.RandomAccessBuffer;
import com.tom_roush.pdfbox.io.RandomAccessRead;
import com.tom_roush.pdfbox.io.SequenceRandomAccessRead;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.util.List;
import java.util.Vector;

/**
 * This will take an array of streams and sequence them together.
 *
 * @author Ben Litchfield
 */
public class COSStreamArray extends COSStream
{
    private COSArray streams;

    /**
     * The first stream will be used to delegate some of the methods for this
     * class.
     */
    private COSStream firstStream;

    /**
     * Constructor.
     *
     * @param array The array of COSStreams to concatenate together.
     */
    public COSStreamArray( COSArray array )
    {
        super( new COSDictionary() );
        streams = array;
        if( array.size() > 0 )
        {
            firstStream = (COSStream)array.getObject( 0 );
        }
    }

    /**
     * This will get a stream (or the reference to a stream) from the array.
     *
     * @param index The index of the requested stream
     * @return The stream object or a reference to a stream
     */
    public COSBase get( int index )
    {
        return streams.get( index );
    }

    /**
     * This will get the number of streams in the array.
     *
     * @return the number of streams
     */
    public int getStreamCount()
    {
        return streams.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public COSBase getItem( COSName key )
    {
        return firstStream.getItem( key );
    }

   /**
    * {@inheritDoc}
     */
    @Override
    public COSBase getDictionaryObject( COSName key )
    {
        return firstStream.getDictionaryObject( key );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "COSStream{}";
    }

    /**
     * This will get the dictionary that is associated with this stream.
     *
     * @return the object that is associated with this stream.
     */
    public COSDictionary getDictionary()
    {
        return firstStream;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getFilteredStream() throws IOException
    {
        throw new IOException( "Error: Not allowed to get filtered stream from array of streams." );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RandomAccessRead getFilteredRandomAccess() throws IOException
    {
        throw new IOException("Error: Not allowed to get filtered stream from array of streams.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getUnfilteredStream() throws IOException
    {
        Vector<InputStream> inputStreams = new Vector<InputStream>();
        byte[] inbetweenStreamBytes = "\n".getBytes("ISO-8859-1");

        for( int i=0;i<streams.size(); i++ )
        {
            COSStream stream = (COSStream)streams.getObject( i );
            inputStreams.add( stream.getUnfilteredStream() );
            //handle the case where there is no whitespace in the
            //between streams in the contents array, without this
            //it is possible that two operators will get concatenated
            //together
            inputStreams.add( new ByteArrayInputStream( inbetweenStreamBytes ) );
        }

        return new SequenceInputStream( inputStreams.elements() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RandomAccessRead getUnfilteredRandomAccess() throws IOException
    {
        List<RandomAccessRead> input = new Vector<RandomAccessRead>();
        byte[] inbetweenStreamBytes = "\n".getBytes("ISO-8859-1");

        for (int i = 0; i < streams.size(); i++)
        {
            COSStream stream = (COSStream) streams.getObject(i);
            RandomAccessRead randomAccess = stream.getUnfilteredRandomAccess();
            // omit empty streams
            if (randomAccess.length() > 0)
            {
                input.add(randomAccess);
                //handle the case where there is no whitespace in the
                //between streams in the contents array, without this
                //it is possible that two operators will get concatenated
                //together
                input.add(new RandomAccessBuffer(inbetweenStreamBytes));
            }
        }
        return new SequenceRandomAccessRead(input);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object accept(ICOSVisitor visitor) throws IOException
    {
        return streams.accept( visitor );
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public COSBase getFilters()
    {
        return firstStream.getFilters();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OutputStream createFilteredStream() throws IOException
    {
        return firstStream.createFilteredStream();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OutputStream createFilteredStream( COSBase expectedLength ) throws IOException
    {
        return firstStream.createFilteredStream( expectedLength );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFilters(COSBase filters) throws IOException
    {
        //should this be allowed?  Should this
        //propagate to all streams in the array?
        firstStream.setFilters( filters );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OutputStream createUnfilteredStream() throws IOException
    {
        return firstStream.createUnfilteredStream();
    }

    /**
     * Appends a new stream to the array that represents this object's stream.
     *
     * @param streamToAppend The stream to append.
     */
    public void appendStream(COSStream streamToAppend)
    {
        streams.add(streamToAppend);
    }
    
    /**
     * Insert the given stream at the beginning of the existing stream array.
     * @param streamToBeInserted
     */
    public void insertCOSStream(PDStream streamToBeInserted)
    {
        COSArray tmp = new COSArray();
        tmp.add(streamToBeInserted);
        tmp.addAll(streams);
        streams.clear();
        streams = tmp;
    }

}

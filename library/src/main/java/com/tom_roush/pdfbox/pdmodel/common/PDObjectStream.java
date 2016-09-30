package org.apache.pdfbox.pdmodel.common;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;

import org.apache.pdfbox.pdmodel.PDDocument;



/**
 * A PDStream represents a stream in a PDF document.  Streams are tied to a single
 * PDF document.
 *
 * @author Ben Litchfield
 */
public class PDObjectStream extends PDStream
{

    /**
     * Constructor.
     *
     * @param str The stream parameter.
     */
    public PDObjectStream( COSStream str )
    {
        super( str );
    }

    /**
     * This will create a new PDObjectStream object.
     *
     * @param document The document that the stream will be part of.
     * @return A new stream object.
     */
    public static PDObjectStream createStream( PDDocument document )
    {
        COSStream cosStream = document.getDocument().createCOSStream();
        PDObjectStream strm = new PDObjectStream( cosStream );
        strm.getStream().setItem( COSName.TYPE, COSName.OBJ_STM );
        return strm;
    }

    /**
     * Get the type of this object, should always return "ObjStm".
     *
     * @return The type of this object.
     */
    public String getType()
    {
        return getStream().getNameAsString( COSName.TYPE );
    }

    /**
     * Get the number of compressed object.
     *
     * @return The number of compressed objects.
     */
    public int getNumberOfObjects()
    {
        return getStream().getInt( COSName.N, 0 );
    }

    /**
     * Set the number of objects.
     *
     * @param n The new number of objects.
     */
    public void setNumberOfObjects( int n )
    {
        getStream().setInt( COSName.N, n );
    }

    /**
     * The byte offset (in the decoded stream) of the first compressed object.
     *
     * @return The byte offset to the first object.
     */
    public int getFirstByteOffset()
    {
        return getStream().getInt( COSName.FIRST, 0 );
    }

    /**
     * The byte offset (in the decoded stream) of the first compressed object.
     *
     * @param n The byte offset to the first object.
     */
    public void setFirstByteOffset( int n )
    {
        getStream().setInt( COSName.FIRST, n );
    }

    /**
     * A reference to an object stream, of which the current object stream is
     * considered an extension.
     *
     * @return The object that this stream is an extension.
     */
    public PDObjectStream getExtends()
    {
        PDObjectStream retval = null;
        COSStream stream = (COSStream)getStream().getDictionaryObject( COSName.EXTENDS );
        if( stream != null )
        {
            retval = new PDObjectStream( stream );
        }
        return retval;

    }

    /**
     * A reference to an object stream, of which the current object stream is
     * considered an extension.
     *
     * @param stream The object stream extension.
     */
    public void setExtends( PDObjectStream stream )
    {
        getStream().setItem( COSName.EXTENDS, stream );
    }
}

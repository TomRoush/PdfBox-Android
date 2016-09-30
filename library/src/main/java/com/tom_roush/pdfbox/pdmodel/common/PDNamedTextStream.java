package org.apache.pdfbox.pdmodel.common;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;

/**
 * A named text stream is a combination of a name and a PDTextStream object.  This
 * is used in name trees.
 *
 * @author Ben Litchfield
 */
public class PDNamedTextStream implements DualCOSObjectable
{
    private COSName streamName;
    private PDTextStream stream;

    /**
     * Constructor.
     */
    public PDNamedTextStream()
    {
        //default constructor
    }

    /**
     * Constructor.
     *
     * @param name The name of the stream.
     * @param str The stream.
     */
    public PDNamedTextStream( COSName name, COSBase str )
    {
        streamName = name;
        stream = PDTextStream.createTextStream( str );
    }

    /**
     * The name of the named text stream.
     *
     * @return The stream name.
     */
    public String getName()
    {
        String name = null;
        if( streamName != null )
        {
            name = streamName.getName();
        }
        return name;
    }

    /**
     * This will set the name of the named text stream.
     *
     * @param name The name of the named text stream.
     */
    public void setName( String name )
    {
        streamName = COSName.getPDFName( name );
    }

    /**
     * This will get the stream.
     *
     * @return The stream associated with this name.
     */
    public PDTextStream getStream()
    {
        return stream;
    }

    /**
     * This will set the stream.
     *
     * @param str The stream associated with this name.
     */
    public void setStream( PDTextStream str )
    {
        stream = str;
    }

    /**
     * Convert this standard java object to a COS object.
     *
     * @return The cos object that matches this Java object.
     */
    @Override
    public COSBase getFirstCOSObject()
    {
        return streamName;
    }

    /**
     * Convert this standard java object to a COS object.
     *
     * @return The cos object that matches this Java object.
     */
    @Override
    public COSBase getSecondCOSObject()
    {
        COSBase retval = null;
        if( stream != null )
        {
            retval = stream.getCOSObject();
        }
        return retval;
    }
}

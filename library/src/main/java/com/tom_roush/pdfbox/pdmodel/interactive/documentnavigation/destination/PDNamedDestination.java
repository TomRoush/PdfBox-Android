package com.tom_roush.pdfbox.pdmodel.interactive.documentnavigation.destination;

import java.io.IOException;

import com.tom_roush.pdfbox.cos.COSBase;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.cos.COSString;

/**
 * This represents a destination to a page by referencing it with a name.
 *
 * @author Ben Litchfield
 */
public class PDNamedDestination extends PDDestination
{
    private COSBase namedDestination;

    /**
     * Constructor.
     *
     * @param dest The named destination.
     */
    public PDNamedDestination( COSString dest )
    {
        namedDestination = dest;
    }

    /**
     * Constructor.
     *
     * @param dest The named destination.
     */
    public PDNamedDestination( COSName dest )
    {
        namedDestination = dest;
    }

    /**
     * Default constructor.
     */
    public PDNamedDestination()
    {
        //default, so do nothing
    }

    /**
     * Default constructor.
     *
     * @param dest The named destination.
     */
    public PDNamedDestination( String dest )
    {
        namedDestination = new COSString( dest );
    }

    /**
     * Convert this standard java object to a COS object.
     *
     * @return The cos object that matches this Java object.
     */
    public COSBase getCOSObject()
    {
        return namedDestination;
    }

    /**
     * This will get the name of the destination.
     *
     * @return The name of the destination.
     */
    public String getNamedDestination()
    {
        String retval = null;
        if( namedDestination instanceof COSString )
        {
            retval = ((COSString)namedDestination).getString();
        }
        else if( namedDestination instanceof COSName )
        {
            retval = ((COSName)namedDestination).getName();
        }

        return retval;
    }

    /**
     * Set the named destination.
     *
     * @param dest The new named destination.
     *
     * @throws IOException If there is an error setting the named destination.
     */
    public void setNamedDestination( String dest ) throws IOException
    {
        if( dest == null )
        {
            namedDestination = null;
        }
        else
        {
            namedDestination = new COSString( dest );
        }
    }

}

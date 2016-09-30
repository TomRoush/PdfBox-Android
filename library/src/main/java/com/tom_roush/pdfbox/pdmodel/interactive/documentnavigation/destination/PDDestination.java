package org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination;

import java.io.IOException;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.common.PDDestinationOrAction;

/**
 * This represents a destination in a PDF document.
 *
 * @author Ben Litchfield
 */
public abstract class PDDestination implements PDDestinationOrAction
{

    /**
     * This will create a new destination depending on the type of COSBase
     * that is passed in.
     *
     * @param base The base level object.
     *
     * @return A new destination.
     *
     * @throws IOException If the base cannot be converted to a Destination.
     */
    public static PDDestination create( COSBase base ) throws IOException
    {
        PDDestination retval = null;
        if( base == null )
        {
            //this is ok, just return null.
        }
        else if (base instanceof COSArray
        		&& ((COSArray) base).size() > 1
        		&& ((COSArray) base).getObject(1) instanceof COSName)
        {
            COSArray array = (COSArray)base;
            COSName type = (COSName) array.getObject(1);
            String typeString = type.getName();
            if( typeString.equals( PDPageFitDestination.TYPE ) ||
                typeString.equals( PDPageFitDestination.TYPE_BOUNDED ))
            {
                retval = new PDPageFitDestination( array );
            }
            else if( typeString.equals( PDPageFitHeightDestination.TYPE ) ||
                     typeString.equals( PDPageFitHeightDestination.TYPE_BOUNDED ))
            {
                retval = new PDPageFitHeightDestination( array );
            }
            else if( typeString.equals( PDPageFitRectangleDestination.TYPE ) )
            {
                retval = new PDPageFitRectangleDestination( array );
            }
            else if( typeString.equals( PDPageFitWidthDestination.TYPE ) ||
                     typeString.equals( PDPageFitWidthDestination.TYPE_BOUNDED ))
            {
                retval = new PDPageFitWidthDestination( array );
            }
            else if( typeString.equals( PDPageXYZDestination.TYPE ) )
            {
                retval = new PDPageXYZDestination( array );
            }
            else
            {
            	throw new IOException( "Unknown destination type: " + type.getName() );
            }
        }
        else if( base instanceof COSString )
        {
            retval = new PDNamedDestination( (COSString)base );
        }
        else if( base instanceof COSName )
        {
            retval = new PDNamedDestination( (COSName)base );
        }
        else
        {
            throw new IOException( "Error: can't convert to Destination " + base );
        }
        return retval;
    }
}

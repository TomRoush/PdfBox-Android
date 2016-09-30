package org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination;

import org.apache.pdfbox.cos.COSArray;

/**
 * This represents a destination to a page and the page contents will be magnified to just
 * fit on the screen.
 *
 * @author Ben Litchfield
 */
public class PDPageFitDestination extends PDPageDestination
{
    /**
     * The type of this destination.
     */
    protected static final String TYPE = "Fit";
    /**
     * The type of this destination.
     */
    protected static final String TYPE_BOUNDED = "FitB";

    /**
     * Default constructor.
     *
     */
    public PDPageFitDestination()
    {
        super();
        array.growToSize(2);
        array.setName( 1, TYPE );

    }

    /**
     * Constructor from an existing destination array.
     *
     * @param arr The destination array.
     */
    public PDPageFitDestination( COSArray arr )
    {
        super( arr );
    }

    /**
     * A flag indicating if this page destination should just fit bounding box of the PDF.
     *
     * @return true If the destination should fit just the bounding box.
     */
    public boolean fitBoundingBox()
    {
        return TYPE_BOUNDED.equals( array.getName( 1 ) );
    }

    /**
     * Set if this page destination should just fit the bounding box.  The default is false.
     *
     * @param fitBoundingBox A flag indicating if this should fit the bounding box.
     */
    public void setFitBoundingBox( boolean fitBoundingBox )
    {
        array.growToSize( 2 );
        if( fitBoundingBox )
        {
            array.setName( 1, TYPE_BOUNDED );
        }
        else
        {
            array.setName( 1, TYPE );
        }
    }
}

package com.tom_roush.pdfbox.exceptions;

import java.io.IOException;

/**
 * This exception will be thrown when a local destination(page within the same PDF) is required
 * but the bookmark(PDOutlineItem) refers to an external destination or an action that does not
 * point to a page.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.2 $
 */
public class OutlineNotLocalException extends IOException
{

    /**
     * Constructor.
     *
     * @param msg An error message.
     */
    public OutlineNotLocalException( String msg )
    {
        super( msg );
    }
}

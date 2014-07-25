package org.apache.pdfbox.exceptions;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * An simple class that allows a sub exception to be stored.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.4 $
 */
public class WrappedIOException extends IOException
{
    /**
     * constructor comment.
     *
     * @param e The root exception that caused this exception.
     */
    public WrappedIOException( Throwable e )
    {
        initCause( e );
    }

    /**
     * constructor comment.
     *
     * @param message Descriptive text for the exception.
     * @param e The root exception that caused this exception.
     */
    public WrappedIOException( String message, Throwable e )
    {
        super( message );
        initCause( e );
    }
}

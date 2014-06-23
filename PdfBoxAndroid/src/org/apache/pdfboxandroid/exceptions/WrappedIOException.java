package org.apache.pdfboxandroid.exceptions;

import java.io.IOException;

public class WrappedIOException extends IOException {
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

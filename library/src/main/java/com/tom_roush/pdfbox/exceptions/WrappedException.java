package com.tom_roush.pdfbox.exceptions;

/**
 * An exception that that holds a sub exception.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.2 $
 * 
 * @deprecated  java.lang.Exception itself has wrapper capabilities since Java 1.4
 */
public class WrappedException extends Exception
{
    /**
     * constructor comment.
     *
     * @param e The root exception that caused this exception.
     */
    public WrappedException( Exception e )
    {
        super( e );
    }
}

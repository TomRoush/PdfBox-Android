package org.apache.pdfbox.exceptions;

/**
 * An exception that indicates that something has gone wrong during a
 * cryptography operation.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.4 $
 */
public class CryptographyException extends Exception
{
    private Exception embedded;

    /**
     * Constructor.
     *
     * @param msg A msg to go with this exception.
     */
    public CryptographyException( String msg )
    {
        super( msg );
    }

    /**
     * Constructor.
     *
     * @param e The root exception that caused this exception.
     */
    public CryptographyException( Exception e )
    {
        super( e.getMessage() );
        setEmbedded( e );
    }
    /**
     * This will get the exception that caused this exception.
     *
     * @return The embedded exception if one exists.
     */
    public Exception getEmbedded()
    {
        return embedded;
    }
    /**
     * This will set the exception that caused this exception.
     *
     * @param e The sub exception.
     */
    private void setEmbedded( Exception e )
    {
        embedded = e;
    }
}

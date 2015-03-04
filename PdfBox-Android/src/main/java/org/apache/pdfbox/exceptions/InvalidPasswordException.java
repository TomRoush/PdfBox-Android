package org.apache.pdfbox.exceptions;

/**
 * An exception that indicates an invalid password was supplied.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.4 $
 */
public class InvalidPasswordException extends Exception
{

    /**
     * Constructor.
     *
     * @param msg A msg to go with this exception.
     */
    public InvalidPasswordException( String msg )
    {
        super( msg );
    }
}

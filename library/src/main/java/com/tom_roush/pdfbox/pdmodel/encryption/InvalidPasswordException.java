package org.apache.pdfbox.pdmodel.encryption;

import java.io.IOException;

/**
 * Indicates that an invalid password was supplied.
 * @author Ben Litchfield
 */
public class InvalidPasswordException extends IOException
{
    /**
     * Creates a new InvalidPasswordException.
     * @param message A msg to go with this exception.
     */
    InvalidPasswordException( String message )
    {
        super( message );
    }
}

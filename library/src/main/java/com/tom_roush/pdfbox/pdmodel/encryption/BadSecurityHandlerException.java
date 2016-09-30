package org.apache.pdfbox.pdmodel.encryption;

/**
 * This exception can be thrown by the SecurityHandlersManager class when
 * a document required an unimplemented security handler to be opened.
 *
 * @author Benoit Guillon (benoit.guillon@snv.jussieu.fr)
 * @version $Revision: 1.2 $
 */

public class BadSecurityHandlerException extends Exception
{
    /**
     * Default Constructor.
     */
    public BadSecurityHandlerException()
    {
        super();
    }

    /**
     * Constructor.
     *
     * @param e A sub exception.
     */
    public BadSecurityHandlerException(Exception e)
    {
        super(e);
    }

    /**
     * Constructor.
     *
     * @param msg Message describing exception.
     */
    public BadSecurityHandlerException(String msg)
    {
        super(msg);
    }

}

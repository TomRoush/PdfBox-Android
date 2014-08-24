package org.apache.pdfbox.util;

/**
 * This class deals with some logging that is not handled by the log4j replacement.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.3 $
 */
public class ErrorLogger
{
    /**
     * Utility class, should not be instantiated.
     *
     */
    private ErrorLogger()
    {
    }

    /**
     * Log an error message.  This is only used for log4j replacement and
     * should never be used when writing code.
     *
     * @param errorMessage The error message.
     */
    public static void log( String errorMessage )
    {
        System.err.println( errorMessage );
    }

    /**
     * Log an error message.  This is only used for log4j replacement and
     * should never be used when writing code.
     *
     * @param errorMessage The error message.
     * @param t The exception.
     */
    public static void log( String errorMessage, Throwable t )
    {
        System.err.println( errorMessage );
        t.printStackTrace();
    }
}

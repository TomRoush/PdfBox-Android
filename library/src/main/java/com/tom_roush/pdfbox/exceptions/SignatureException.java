package com.tom_roush.pdfbox.exceptions;

/**
 * An exception that indicates a problem during the signing process.
 *
 * @author Thomas Chojecki
 * @version $Revision: $
 */
public class SignatureException extends Exception
{

    public final static int WRONG_PASSWORD = 1;

    public final static int UNSUPPORTED_OPERATION = 2;
    
    public final static int CERT_PATH_CHECK_INVALID = 3;
    
    public final static int NO_SUCH_ALGORITHM = 4;
    
    public final static int INVALID_PAGE_FOR_SIGNATURE = 5;

    public final static int VISUAL_SIGNATURE_INVALID = 6;

    private int no;
  
    /**
     * Constructor.
     *
     * @param msg A msg to go with this exception.
     */
    public SignatureException( String msg )
    {
        super( msg );
    }

    /**
     * Constructor.
     * 
     * @param errno A error number to fulfill this exception
     * @param msg A msg to go with this exception.
     */
    public SignatureException( int errno , String msg ) 
    {
      super( msg );
      no = errno;
    }

    /**
     * Constructor.
     * 
     * @param e The exception that should be encapsulate.
     */
    public SignatureException(Throwable e) 
    {
      super(e);
    }
    
    /**
     * Constructor.
     * 
     * @param errno A error number to fulfill this exception
     * @param e The exception that should be encapsulate.
     */
    public SignatureException( int errno, Throwable e) 
    {
      super(e);
    }

    /**
     * A error number to fulfill this exception
     * 
     * @return the error number if available, otherwise 0
     */
    public int getErrNo() 
    {
      return no;
    }
}

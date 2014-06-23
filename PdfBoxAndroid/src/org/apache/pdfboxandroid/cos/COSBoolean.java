package org.apache.pdfboxandroid.cos;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.pdfboxandroid.exceptions.COSVisitorException;

/**
 * This class represents a boolean value in the PDF document.
 *
 * @author <a href="ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.14 $
 */
public class COSBoolean extends COSBase {
	/**
     * The true boolean token.
     */
    public static final byte[] TRUE_BYTES = new byte[]{ 116, 114, 117, 101 }; //"true".getBytes( "ISO-8859-1" );
    /**
     * The false boolean token.
     */
    public static final byte[] FALSE_BYTES = new byte[]{ 102, 97, 108, 115, 101 }; //"false".getBytes( "ISO-8859-1" );
	
	/**
     * The PDF true value.
     */
    public static final COSBoolean TRUE = new COSBoolean( true );

    /**
     * The PDF false value.
     */
    public static final COSBoolean FALSE = new COSBoolean( false );
    
    private boolean value;

    /**
     * Constructor.
     *
     * @param aValue The boolean value.
     */
    private COSBoolean(boolean aValue )
    {
        value = aValue;
    }
    
    /**
     * This will write this object out to a PDF stream.
     *
     * @param output The stream to write this object out to.
     *
     * @throws IOException If an error occurs while writing out this object.
     */
    public void writePDF( OutputStream output ) throws IOException
    {
        if( value )
        {
            output.write( TRUE_BYTES );
        }
        else
        {
            output.write( FALSE_BYTES );
        }
    }

    /**
     * visitor pattern double dispatch method.
     *
     * @param visitor The object to notify when visiting this object.
     * @return any object, depending on the visitor implementation, or null
     * @throws COSVisitorException If an error occurs while visiting this object.
     */
    public Object accept(ICOSVisitor  visitor) throws COSVisitorException
    {
        return visitor.visitFromBoolean(this);
    }
}

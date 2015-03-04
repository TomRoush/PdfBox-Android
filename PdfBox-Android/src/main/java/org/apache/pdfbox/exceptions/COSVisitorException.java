package org.apache.pdfbox.exceptions;

/**
 * An exception that represents something gone wrong when visiting a PDF object.
 *
 * @author Michael Traut
 * @version $Revision: 1.6 $
 */
public class COSVisitorException extends WrappedException
{

    /**
     * COSVisitorException constructor comment.
     *
     * @param e The root exception that caused this exception.
     */
    public COSVisitorException( Exception e )
    {
        super( e );
    }


}

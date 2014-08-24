package org.apache.pdfbox.util.operator;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.util.PDFOperator;

/**
 * @author Huault : huault@free.fr
 * @version $Revision: 1.4 $
 */

public class ShowText extends OperatorProcessor
{

    /**
     * Tj show Show text.
     * @param operator The operator that is being executed.
     * @param arguments List
     *
     * @throws IOException If there is an error processing this operator.
     */
    public void process(PDFOperator operator, List<COSBase> arguments) throws IOException
    {
        COSString string = (COSString)arguments.get( 0 );
        context.processEncodedText( string.getBytes() );
    }

}

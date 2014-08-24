package org.apache.pdfbox.util.operator;

import java.util.List;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.PDFOperator;

/**
 * @author Huault : huault@free.fr
 * @version $Revision: 1.4 $
 */

public class SetMatrix extends OperatorProcessor
{

    /**
     * Tm Set text matrix and text line matrix.
     * @param operator The operator that is being executed.
     * @param arguments List
     */
    public void process(PDFOperator operator, List<COSBase> arguments)
    {
        //Set text matrix and text line matrix
        COSNumber a = (COSNumber)arguments.get( 0 );
        COSNumber b = (COSNumber)arguments.get( 1 );
        COSNumber c = (COSNumber)arguments.get( 2 );
        COSNumber d = (COSNumber)arguments.get( 3 );
        COSNumber e = (COSNumber)arguments.get( 4 );
        COSNumber f = (COSNumber)arguments.get( 5 );

        Matrix textMatrix = new Matrix();
        textMatrix.setValue( 0, 0, a.floatValue() );
        textMatrix.setValue( 0, 1, b.floatValue() );
        textMatrix.setValue( 1, 0, c.floatValue() );
        textMatrix.setValue( 1, 1, d.floatValue() );
        textMatrix.setValue( 2, 0, e.floatValue() );
        textMatrix.setValue( 2, 1, f.floatValue() );
        context.setTextMatrix( textMatrix );
        context.setTextLineMatrix( textMatrix.copy() );
    }
}

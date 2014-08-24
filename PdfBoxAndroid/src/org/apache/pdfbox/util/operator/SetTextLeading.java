package org.apache.pdfbox.util.operator;

import java.util.List;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.util.PDFOperator;

/**
 * @author Huault : huault@free.fr
 * @version $Revision: 1.4 $
 */

public class SetTextLeading extends OperatorProcessor
{
    /**
     * TL Set text leading.
     * @param operator The operator that is being executed.
     * @param arguments List
     */
    public void process(PDFOperator operator, List<COSBase> arguments)
    {
        COSNumber leading = (COSNumber)arguments.get( 0 );
        context.getGraphicsState().getTextState().setLeading( leading.floatValue() );
    }

}

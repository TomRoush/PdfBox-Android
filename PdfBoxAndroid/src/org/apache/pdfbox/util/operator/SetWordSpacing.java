package org.apache.pdfbox.util.operator;

import java.util.List;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.util.PDFOperator;

/**
 * @author Huault : huault@free.fr
 * @version $Revision: 1.4 $
 */

public class SetWordSpacing extends OperatorProcessor
{
    /**
     * Tw Set word spacing.
     * @param operator The operator that is being executed.
     * @param arguments List
     */
    public void process(PDFOperator operator, List<COSBase> arguments)
    {
        //set word spacing
        COSNumber wordSpacing = (COSNumber)arguments.get( 0 );
        context.getGraphicsState().getTextState().setWordSpacing( wordSpacing.floatValue() );
    }

}

package org.apache.pdfbox.contentstream.operator.text;

import java.util.List;

import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSNumber;

/**
 * TL: Set text leading.
 *
 * @author Laurent Huault
 */
public class SetTextLeading extends OperatorProcessor
{
    @Override
    public void process(Operator operator, List<COSBase> arguments)
    {
        COSNumber leading = (COSNumber)arguments.get( 0 );
        context.getGraphicsState().getTextState().setLeading( leading.floatValue() );
    }

    @Override
    public String getName()
    {
        return "TL";
    }
}

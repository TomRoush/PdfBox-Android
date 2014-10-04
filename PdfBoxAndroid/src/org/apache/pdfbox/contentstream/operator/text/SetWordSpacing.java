package org.apache.pdfbox.contentstream.operator.text;

import java.util.List;

import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSNumber;

/**
 * Tw: Set word spacing.
 *
 * @author Laurent Huault
 */
public class SetWordSpacing extends OperatorProcessor
{
    @Override
    public void process(Operator operator, List<COSBase> arguments)
    {
        COSNumber wordSpacing = (COSNumber)arguments.get( 0 );
        context.getGraphicsState().getTextState().setWordSpacing( wordSpacing.floatValue() );
    }

    @Override
    public String getName()
    {
        return "Tw";
    }
}

package org.apache.pdfbox.contentstream.operator.state;

import java.util.List;

import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSNumber;

import java.io.IOException;

/**
 * w: Set line width.
 *
 * @author Ben Litchfield
 */
public class SetLineWidth extends OperatorProcessor
{
    @Override
    public void process(Operator operator, List<COSBase> arguments) throws IOException
    {
        COSNumber width = (COSNumber)arguments.get( 0 );
        context.getGraphicsState().setLineWidth( width.floatValue() );
    }

    @Override
    public String getName()
    {
        return "w";
    }
}

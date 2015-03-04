package org.apache.pdfbox.contentstream.operator.state;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSNumber;

/**
 * M: Set miter limit.
 */
public class SetLineMiterLimit extends OperatorProcessor
{
    @Override
    public void process(Operator operator, List<COSBase> arguments) throws IOException
    {
        COSNumber miterLimit = (COSNumber)arguments.get( 0 );
        context.getGraphicsState().setMiterLimit( miterLimit.floatValue() );
    }

    @Override
    public String getName()
    {
        return "M";
    }
}

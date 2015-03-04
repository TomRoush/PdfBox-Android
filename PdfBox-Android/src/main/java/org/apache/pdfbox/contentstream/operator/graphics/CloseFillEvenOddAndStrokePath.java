package org.apache.pdfbox.contentstream.operator.graphics;

import java.util.List;

import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSBase;

import java.io.IOException;

/**
 * b* Close, fill and stroke the path with even-odd winding rule.
 */
public final class CloseFillEvenOddAndStrokePath extends GraphicsOperatorProcessor
{
    @Override
    public void process(Operator operator, List<COSBase> operands) throws IOException
    {
        context.processOperator("h", operands);  // ClosePath
        context.processOperator("B*", operands); // FillEvenOddAndStroke
    }

    @Override
    public String getName()
    {
        return "b*";
    }
}

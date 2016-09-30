package com.tom_roush.pdfbox.contentstream.operator.graphics;

import java.io.IOException;
import java.util.List;

import com.tom_roush.pdfbox.contentstream.operator.Operator;
import com.tom_roush.pdfbox.cos.COSBase;

/**
 * n End the path.
 *
 * @author Ben Litchfield
 */
public final class EndPath extends GraphicsOperatorProcessor
{
    @Override
    public void process(Operator operator, List<COSBase> operands) throws IOException
    {
        context.endPath();
    }

    @Override
    public String getName()
    {
        return "n";
    }
}

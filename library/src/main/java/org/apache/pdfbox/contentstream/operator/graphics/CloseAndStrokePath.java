package org.apache.pdfbox.contentstream.operator.graphics;

import java.util.List;

import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSBase;

import java.io.IOException;

/**
 * s: close and stroke the path.
 *
 * @author Ben Litchfield
 */
public class CloseAndStrokePath extends GraphicsOperatorProcessor
{
    @Override
    public void process(Operator operator, List<COSBase> arguments) throws IOException
    {
        context.processOperator( "h", arguments );
        context.processOperator( "S", arguments );
    }

    @Override
    public String getName()
    {
        return "s";
    }
}

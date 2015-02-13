package org.apache.pdfbox.contentstream.operator.state;

import java.util.List;

import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.cos.COSBase;

/**
 * q: Save the graphics state.
 *
 * @author Laurent Huault
 */
public class Save extends OperatorProcessor
{
    @Override
    public void process(Operator operator, List<COSBase> arguments)
    {
        context.saveGraphicsState();
    }

    @Override
    public String getName()
    {
        return "q";
    }
}

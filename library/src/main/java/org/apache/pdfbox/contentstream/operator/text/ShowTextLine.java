package org.apache.pdfbox.contentstream.operator.text;

import java.util.List;

import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.cos.COSBase;

import java.io.IOException;

/**
 * ': Move to the next line and show text.
 *
 * @author Laurent Huault
 */
public class ShowTextLine extends OperatorProcessor
{
    @Override
    public void process(Operator operator, List<COSBase> arguments) throws IOException
    {
        context.processOperator("T*", null);
        context.processOperator("Tj", arguments);
    }

    @Override
    public String getName()
    {
        return "'";
    }
}

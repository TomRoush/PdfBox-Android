package org.apache.pdfbox.contentstream.operator.text;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.util.Matrix;

/**
 * BT: Begin text.
 *
 * @author Ben Litchfield
 * @author Laurent Huault
 */
public class BeginText extends OperatorProcessor
{
    @Override
    public void process(Operator operator, List<COSBase> arguments) throws IOException
    {
        context.setTextMatrix( new Matrix());
        context.setTextLineMatrix( new Matrix() );
        context.beginText();
    }

    @Override
    public String getName()
    {
        return "BT";
    }
}

package org.apache.pdfbox.contentstream.operator.state;

import java.util.List;

import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSNumber;

import java.io.IOException;

/**
 * J: Set the line cap style.
 *
 * @author Andreas Lehmkühler
 */
public class SetLineCapStyle extends OperatorProcessor
{
    @Override
    public void process(Operator operator, List<COSBase> arguments) throws IOException
    {
        int lineCapStyle = ((COSNumber)arguments.get( 0 )).intValue();
        context.getGraphicsState().setLineCap( lineCapStyle );
    }

    @Override
    public String getName()
    {
        return "J";
    }
}

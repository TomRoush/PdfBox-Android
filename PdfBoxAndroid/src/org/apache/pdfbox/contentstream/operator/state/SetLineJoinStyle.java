package org.apache.pdfbox.contentstream.operator.state;

import java.util.List;

import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSNumber;

import java.io.IOException;

/**
 * j: Set the line join style.
 *
 * @author Andreas Lehmkühler
 */
public class SetLineJoinStyle extends OperatorProcessor
{
    @Override
    public void process(Operator operator, List<COSBase> arguments) throws IOException
    {
        int lineJoinStyle = ((COSNumber)arguments.get( 0 )).intValue();
        context.getGraphicsState().setLineJoin( lineJoinStyle );
    }

    @Override
    public String getName()
    {
        return "j";
    }
}

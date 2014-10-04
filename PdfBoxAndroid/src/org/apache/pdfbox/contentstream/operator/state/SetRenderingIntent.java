package org.apache.pdfbox.contentstream.operator.state;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.graphics.state.RenderingIntent;

/**
 * ri: Set the rendering intent.
 *
 * @author John Hewson
 */
public class SetRenderingIntent extends OperatorProcessor
{
    @Override
    public void process(Operator operator, List<COSBase> operands) throws IOException
    {
        COSName value = (COSName)operands.get(0);
        context.getGraphicsState().setRenderingIntent(RenderingIntent.fromString(value.getName()));
    }

    @Override
    public String getName()
    {
        return "ri";
    }
}

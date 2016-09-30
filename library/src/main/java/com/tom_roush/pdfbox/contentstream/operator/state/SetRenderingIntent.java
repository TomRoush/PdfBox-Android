package com.tom_roush.pdfbox.contentstream.operator.state;

import java.io.IOException;
import java.util.List;

import com.tom_roush.pdfbox.contentstream.operator.Operator;
import com.tom_roush.pdfbox.contentstream.operator.OperatorProcessor;
import com.tom_roush.pdfbox.cos.COSBase;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.pdmodel.graphics.state.RenderingIntent;

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

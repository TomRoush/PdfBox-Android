package com.tom_roush.pdfbox.contentstream.operator.state;

import java.util.List;

import com.tom_roush.pdfbox.contentstream.operator.Operator;
import com.tom_roush.pdfbox.contentstream.operator.OperatorProcessor;
import com.tom_roush.pdfbox.cos.COSBase;

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

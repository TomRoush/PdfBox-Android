package com.tom_roush.pdfbox.contentstream.operator.state;

import java.io.IOException;
import java.util.List;

import com.tom_roush.pdfbox.contentstream.operator.Operator;
import com.tom_roush.pdfbox.contentstream.operator.OperatorProcessor;
import com.tom_roush.pdfbox.cos.COSBase;
import com.tom_roush.pdfbox.cos.COSNumber;

/**
 * M: Set miter limit.
 */
public class SetLineMiterLimit extends OperatorProcessor
{
    @Override
    public void process(Operator operator, List<COSBase> arguments) throws IOException
    {
        COSNumber miterLimit = (COSNumber)arguments.get( 0 );
        context.getGraphicsState().setMiterLimit( miterLimit.floatValue() );
    }

    @Override
    public String getName()
    {
        return "M";
    }
}

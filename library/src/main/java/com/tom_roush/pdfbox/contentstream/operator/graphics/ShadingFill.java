package com.tom_roush.pdfbox.contentstream.operator.graphics;

import java.io.IOException;
import java.util.List;

import com.tom_roush.pdfbox.contentstream.operator.Operator;
import com.tom_roush.pdfbox.cos.COSBase;
import com.tom_roush.pdfbox.cos.COSName;

/**
 * sh Fills the clipping area with the given shading pattern.
 *
 * @author Daniel Wilson
 */
public final class ShadingFill extends GraphicsOperatorProcessor
{
    @Override
    public void process(Operator operator, List<COSBase> operands) throws IOException
    {
        context.shadingFill((COSName) operands.get(0));
    }

    @Override
    public String getName()
    {
        return "sh";
    }
}

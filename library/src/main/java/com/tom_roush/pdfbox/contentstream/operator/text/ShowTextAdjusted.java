package com.tom_roush.pdfbox.contentstream.operator.text;

import java.util.List;

import com.tom_roush.pdfbox.contentstream.operator.Operator;
import com.tom_roush.pdfbox.contentstream.operator.OperatorProcessor;
import com.tom_roush.pdfbox.cos.COSArray;
import com.tom_roush.pdfbox.cos.COSBase;

import java.io.IOException;

/**
 * TJ: Show text, with position adjustments.
 *
 * @author Laurent Huault
 */
public class ShowTextAdjusted extends OperatorProcessor
{
    @Override
    public void process(Operator operator, List<COSBase> arguments) throws IOException
    {
        COSArray array = (COSArray)arguments.get(0);
        context.showTextStrings(array);
    }

    @Override
    public String getName()
    {
        return "TJ";
    }
}

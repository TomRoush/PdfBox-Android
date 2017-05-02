package com.tom_roush.pdfbox.contentstream.operator.text;

import com.tom_roush.pdfbox.contentstream.operator.MissingOperandException;
import com.tom_roush.pdfbox.contentstream.operator.Operator;
import com.tom_roush.pdfbox.contentstream.operator.OperatorProcessor;
import com.tom_roush.pdfbox.cos.COSBase;
import com.tom_roush.pdfbox.cos.COSFloat;
import com.tom_roush.pdfbox.cos.COSNumber;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * TD: Move text position and set leading.
 *
 * @author Laurent Huault
 */
public class MoveTextSetLeading extends OperatorProcessor
{
    @Override
    public void process(Operator operator, List<COSBase> arguments) throws IOException
    {
        if (arguments.size() < 2)
        {
            throw new MissingOperandException(operator, arguments);
        }

        //move text position and set leading
        COSNumber y = (COSNumber) arguments.get(1);

        List<COSBase> args = new ArrayList<COSBase>();
        args.add(new COSFloat(-1 * y.floatValue()));
        context.processOperator("TL", args);
        context.processOperator("Td", arguments);
    }

    @Override
    public String getName()
    {
        return "TD";
    }
}

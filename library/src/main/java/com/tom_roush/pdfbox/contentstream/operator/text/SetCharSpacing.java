package com.tom_roush.pdfbox.contentstream.operator.text;

import com.tom_roush.pdfbox.contentstream.operator.MissingOperandException;
import com.tom_roush.pdfbox.contentstream.operator.Operator;
import com.tom_roush.pdfbox.contentstream.operator.OperatorProcessor;
import com.tom_roush.pdfbox.cos.COSBase;
import com.tom_roush.pdfbox.cos.COSNumber;

import java.io.IOException;
import java.util.List;

/**
 * Tc: Set character spacing.
 *
 * @author Laurent Huault
 */
public class SetCharSpacing extends OperatorProcessor
{
    @Override
    public void process(Operator operator, List<COSBase> arguments) throws IOException
    {
        if (arguments.isEmpty())
        {
            throw new MissingOperandException(operator, arguments);
        }

        // there are some documents which are incorrectly structured, and have
        // a wrong number of arguments to this, so we will assume the last argument
        // in the list
        Object charSpacing = arguments.get(arguments.size() - 1);
        if (charSpacing instanceof COSNumber)
        {
            COSNumber characterSpacing = (COSNumber) charSpacing;
            context.getGraphicsState().getTextState().setCharacterSpacing(characterSpacing.floatValue());
        }
    }

    @Override
    public String getName()
    {
        return "Tc";
    }
}
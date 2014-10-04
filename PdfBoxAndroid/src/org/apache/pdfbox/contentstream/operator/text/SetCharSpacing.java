package org.apache.pdfbox.contentstream.operator.text;

import java.util.List;

import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSNumber;

/**
 * Tc: Set character spacing.
 *
 * @author Laurent Huault
 */
public class SetCharSpacing extends OperatorProcessor
{
    @Override
    public void process(Operator operator, List<COSBase> arguments)
    {
        //set character spacing
        if(arguments.size() > 0)
        {
            //There are some documents which are incorrectly structured, and have
            //a wrong number of arguments to this, so we will assume the last argument
            //in the list
            Object charSpacing = arguments.get(arguments.size()-1);
            if(charSpacing instanceof COSNumber)
            {
                COSNumber characterSpacing = (COSNumber)charSpacing;
                context.getGraphicsState().getTextState().setCharacterSpacing(characterSpacing.floatValue());
            }
        }
    }

    @Override
    public String getName()
    {
        return "Tc";
    }
}

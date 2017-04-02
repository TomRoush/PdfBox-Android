package com.tom_roush.pdfbox.contentstream.operator.state;

import com.tom_roush.pdfbox.contentstream.operator.MissingOperandException;
import com.tom_roush.pdfbox.contentstream.operator.Operator;
import com.tom_roush.pdfbox.contentstream.operator.OperatorProcessor;
import com.tom_roush.pdfbox.cos.COSBase;
import com.tom_roush.pdfbox.cos.COSNumber;
import com.tom_roush.pdfbox.util.Matrix;

import java.util.List;

/**
 * Tm: Set text matrix and text line matrix.
 *
 * @author Laurent Huault
 */
public class SetMatrix extends OperatorProcessor
{
    @Override
    public void process(Operator operator, List<COSBase> arguments) throws MissingOperandException
    {
        if (arguments.size() < 6)
        {
            throw new MissingOperandException(operator, arguments);
        }

        COSNumber a = (COSNumber)arguments.get( 0 );
        COSNumber b = (COSNumber)arguments.get( 1 );
        COSNumber c = (COSNumber)arguments.get( 2 );
        COSNumber d = (COSNumber)arguments.get( 3 );
        COSNumber e = (COSNumber)arguments.get( 4 );
        COSNumber f = (COSNumber)arguments.get( 5 );

        Matrix matrix = new Matrix(a.floatValue(), b.floatValue(), c.floatValue(),
                d.floatValue(), e.floatValue(), f.floatValue());

        context.setTextMatrix(matrix);
        context.setTextLineMatrix(matrix.clone());
    }

    @Override
    public String getName()
    {
        return "Tm";
    }
}

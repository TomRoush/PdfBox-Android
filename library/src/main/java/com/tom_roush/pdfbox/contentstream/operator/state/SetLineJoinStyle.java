package org.apache.pdfbox.contentstream.operator.state;

import android.graphics.Paint;

import java.util.List;

import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSNumber;

import java.io.IOException;

/**
 * j: Set the line join style.
 */
public class SetLineJoinStyle extends OperatorProcessor
{
    @Override
    public void process(Operator operator, List<COSBase> arguments) throws IOException
    {
        Paint.Join lineJoinStyle;
        switch(((COSNumber)arguments.get( 0 )).intValue())  {
            case 0:
                lineJoinStyle = Paint.Join.MITER;
                break;
            case 1:
                lineJoinStyle = Paint.Join.ROUND;
                break;
            case 2:
                lineJoinStyle = Paint.Join.BEVEL;
                break;
            default:
                lineJoinStyle = null;
        }

        context.getGraphicsState().setLineJoin( lineJoinStyle );
    }

    @Override
    public String getName()
    {
        return "j";
    }
}

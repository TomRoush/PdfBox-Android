package com.tom_roush.pdfbox.contentstream.operator.state;

import android.graphics.Paint;

import java.util.List;

import com.tom_roush.pdfbox.contentstream.operator.Operator;
import com.tom_roush.pdfbox.contentstream.operator.OperatorProcessor;
import com.tom_roush.pdfbox.cos.COSBase;
import com.tom_roush.pdfbox.cos.COSNumber;

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

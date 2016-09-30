package com.tom_roush.pdfbox.contentstream.operator.state;

import java.io.IOException;
import java.util.List;

import com.tom_roush.pdfbox.contentstream.operator.Operator;
import com.tom_roush.pdfbox.contentstream.operator.OperatorProcessor;
import com.tom_roush.pdfbox.cos.COSBase;
import com.tom_roush.pdfbox.cos.COSNumber;

import android.graphics.Paint;

/**
 * J: Set the line cap style.
 */
public class SetLineCapStyle extends OperatorProcessor
{
    @Override
    public void process(Operator operator, List<COSBase> arguments) throws IOException
    {
        Paint.Cap lineCapStyle;
        switch(((COSNumber)arguments.get( 0 )).intValue())  {
            case 0:
                lineCapStyle = Paint.Cap.BUTT;
                break;
            case 1:
                lineCapStyle = Paint.Cap.ROUND;
                break;
            case 2:
                lineCapStyle = Paint.Cap.SQUARE;
                break;
            default:
                lineCapStyle = null;
        }

        context.getGraphicsState().setLineCap( lineCapStyle );
    }

    @Override
    public String getName()
    {
        return "J";
    }
}

package com.tom_roush.pdfbox.contentstream.operator.graphics;

import android.graphics.PointF;
import android.util.Log;

import com.tom_roush.pdfbox.contentstream.operator.Operator;
import com.tom_roush.pdfbox.cos.COSBase;
import com.tom_roush.pdfbox.cos.COSNumber;

import java.io.IOException;
import java.util.List;

/**
 * l Append straight line segment to path.
 *
 * @author Ben Litchfield
 */
public class LineTo extends GraphicsOperatorProcessor
{
    @Override
    public void process(Operator operator, List<COSBase> operands) throws IOException
    {
        // append straight line segment from the current point to the point
        COSNumber x = (COSNumber) operands.get(0);
        COSNumber y = (COSNumber) operands.get(1);

        PointF pos = context.transformedPoint(x.floatValue(), y.floatValue());

        if (context.getCurrentPoint() == null)
        {
            Log.w("PdfBox-Android", "LineTo (" + pos.x + "," + pos.y + ") without initial MoveTo");
            context.moveTo(pos.x, pos.y);
        }
        else
        {
            context.lineTo(pos.x, pos.y);
        }
    }

    @Override
    public String getName()
    {
        return "l";
    }
}

package com.tom_roush.pdfbox.contentstream.operator.graphics;

import android.graphics.PointF;
import android.util.Log;

import com.tom_roush.pdfbox.contentstream.operator.Operator;
import com.tom_roush.pdfbox.cos.COSBase;
import com.tom_roush.pdfbox.cos.COSNumber;

import java.io.IOException;
import java.util.List;

/**
 * c Append curved segment to path.
 *
 * @author Ben Litchfield
 */
public class CurveTo extends GraphicsOperatorProcessor
{
    @Override
    public void process(Operator operator, List<COSBase> operands) throws IOException
    {
        COSNumber x1 = (COSNumber) operands.get(0);
        COSNumber y1 = (COSNumber) operands.get(1);
        COSNumber x2 = (COSNumber) operands.get(2);
        COSNumber y2 = (COSNumber) operands.get(3);
        COSNumber x3 = (COSNumber) operands.get(4);
        COSNumber y3 = (COSNumber) operands.get(5);

        PointF point1 = context.transformedPoint(x1.floatValue(), y1.floatValue());
        PointF point2 = context.transformedPoint(x2.floatValue(), y2.floatValue());
        PointF point3 = context.transformedPoint(x3.floatValue(), y3.floatValue());

        if (context.getCurrentPoint() == null)
        {
            Log.w("PdfBox-Android", "curveTo (" + point3.x + "," + point3.y + ") without initial MoveTo");
            context.moveTo(point3.x, point3.y);
        }
        else
        {
            context.curveTo(point1.x, point1.y,
                    point2.x, point2.y,
                    point3.x, point3.y);
        }
    }

    @Override
    public String getName()
    {
        return "c";
    }
}

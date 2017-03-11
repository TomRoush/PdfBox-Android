package com.tom_roush.pdfbox.contentstream.operator.graphics;

import android.graphics.PointF;
import android.util.Log;

import com.tom_roush.pdfbox.contentstream.operator.Operator;
import com.tom_roush.pdfbox.cos.COSBase;
import com.tom_roush.pdfbox.cos.COSNumber;

import java.io.IOException;
import java.util.List;

/**
 * v Append curved segment to path with the initial point replicated.
 *
 * @author Ben Litchfield
 */
public class CurveToReplicateInitialPoint extends GraphicsOperatorProcessor
{
    @Override
    public void process(Operator operator, List<COSBase> operands) throws IOException
    {
        COSNumber x2 = (COSNumber) operands.get(0);
        COSNumber y2 = (COSNumber) operands.get(1);
        COSNumber x3 = (COSNumber) operands.get(2);
        COSNumber y3 = (COSNumber) operands.get(3);

        PointF currentPoint = context.getCurrentPoint();

        PointF point2 = context.transformedPoint(x2.floatValue(), y2.floatValue());
        PointF point3 = context.transformedPoint(x3.floatValue(), y3.floatValue());

        if (currentPoint == null)
        {
            Log.w("PdfBox-Android", "curveTo (" + point3.x + "," + point3.y + ") without initial MoveTo");
            context.moveTo(point3.x, point3.y);
        }
        else
        {
            context.curveTo(currentPoint.x, currentPoint.y,
                    point2.x, point2.y,
                    point3.x, point3.y);
        }
    }

    @Override
    public String getName()
    {
        return "v";
    }
}

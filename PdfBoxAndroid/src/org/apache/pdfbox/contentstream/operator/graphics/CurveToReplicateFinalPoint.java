package org.apache.pdfbox.contentstream.operator.graphics;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSNumber;

import android.graphics.PointF;

/**
 * y Append curved segment to path with final point replicated.
 *
 * @author Ben Litchfield
 */
public final class CurveToReplicateFinalPoint extends GraphicsOperatorProcessor
{
    @Override
    public void process(Operator operator, List<COSBase> operands) throws IOException
    {
        COSNumber x1 = (COSNumber)operands.get(0);
        COSNumber y1 = (COSNumber)operands.get(1);
        COSNumber x3 = (COSNumber)operands.get(2);
        COSNumber y3 = (COSNumber)operands.get(3);

        PointF point1 = context.transformedPoint(x1.doubleValue(), y1.doubleValue());
        PointF point3 = context.transformedPoint(x3.doubleValue(), y3.doubleValue());

        context.curveTo((float) point1.x, (float) point1.y,
                        (float) point3.x, (float) point3.y,
                        (float) point3.x, (float) point3.y);
    }

    @Override
    public String getName()
    {
        return "y";
    }
}

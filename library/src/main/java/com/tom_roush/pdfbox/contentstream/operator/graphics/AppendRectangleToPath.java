package org.apache.pdfbox.contentstream.operator.graphics;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSNumber;

import android.graphics.PointF;

/**
 * re Appends a rectangle to the path.
 *
 * @author Ben Litchfield
 */
public final class AppendRectangleToPath extends GraphicsOperatorProcessor
{
    @Override
    public void process(Operator operator, List<COSBase> operands) throws IOException
    {
        COSNumber x = (COSNumber) operands.get(0);
        COSNumber y = (COSNumber) operands.get(1);
        COSNumber w = (COSNumber) operands.get(2);
        COSNumber h = (COSNumber) operands.get(3);

        float x1 = x.floatValue();
        float y1 = y.floatValue();

        // create a pair of coordinates for the transformation
        float x2 = w.floatValue() + x1;
        float y2 = h.floatValue() + y1;

        PointF p0 = context.transformedPoint(x1, y1);
        PointF p1 = context.transformedPoint(x2, y1);
        PointF p2 = context.transformedPoint(x2, y2);
        PointF p3 = context.transformedPoint(x1, y2);

        context.appendRectangle(p0, p1, p2, p3);
    }

    @Override
    public String getName()
    {
        return "re";
    }
}

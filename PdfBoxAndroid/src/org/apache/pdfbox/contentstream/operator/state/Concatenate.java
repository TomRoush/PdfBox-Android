package org.apache.pdfbox.contentstream.operator.state;

import java.util.List;
import java.io.IOException;

import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.util.Matrix;

/**
 * cm: Concatenate matrix to current transformation matrix.
 *
 * @author Laurent Huault
 */
public class Concatenate extends OperatorProcessor
{
    @Override
    public void process(Operator operator, List<COSBase> arguments) throws IOException
    {
        // concatenate matrix to current transformation matrix
        COSNumber a = (COSNumber) arguments.get(0);
        COSNumber b = (COSNumber) arguments.get(1);
        COSNumber c = (COSNumber) arguments.get(2);
        COSNumber d = (COSNumber) arguments.get(3);
        COSNumber e = (COSNumber) arguments.get(4);
        COSNumber f = (COSNumber) arguments.get(5);

        Matrix newMatrix = new Matrix();
        newMatrix.setValue(0, 0, a.floatValue());
        newMatrix.setValue(0, 1, b.floatValue());
        newMatrix.setValue(1, 0, c.floatValue());
        newMatrix.setValue(1, 1, d.floatValue());
        newMatrix.setValue(2, 0, e.floatValue());
        newMatrix.setValue(2, 1, f.floatValue());

        // this line has changed
        context.getGraphicsState().setCurrentTransformationMatrix(
                newMatrix.multiply(context.getGraphicsState().getCurrentTransformationMatrix()));
    }

    @Override
    public String getName()
    {
        return "cm";
    }
}

package org.apache.pdfbox.contentstream.operator;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.PDFMarkedContentExtractor;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Do: Draws an XObject.
 *
 * @author Ben Litchfield
 * @author Mario Ivankovits
 */
public class DrawObject extends OperatorProcessor
{
    @Override
    public void process(Operator operator, List<COSBase> arguments) throws IOException
    {
        COSName name = (COSName) arguments.get(0);

        Map<String,PDXObject> xobjects = context.getXObjects();
        PDXObject xobject = xobjects.get(name.getName());
        if (context instanceof PDFMarkedContentExtractor)
        {
            ((PDFMarkedContentExtractor) context).xobject(xobject);
        }

        if(xobject instanceof PDFormXObject)
        {
            PDFormXObject form = (PDFormXObject)xobject;

            // if there is an optional form matrix, we have to map the form space to the user space
            Matrix matrix = form.getMatrix();
            if (matrix != null) 
            {
                Matrix xobjectCTM = matrix.multiply(context.getGraphicsState().getCurrentTransformationMatrix());
                context.getGraphicsState().setCurrentTransformationMatrix(xobjectCTM);
            }

            context.showForm(form);
        }
    }

    @Override
    public String getName()
    {
        return "Do";
    }
}

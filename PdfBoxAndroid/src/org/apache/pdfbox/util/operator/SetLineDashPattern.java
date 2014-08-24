package org.apache.pdfbox.util.operator;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.pdmodel.graphics.PDLineDashPattern;
import org.apache.pdfbox.util.PDFOperator;

/**
 * Implementation of content stream operator for page drawer.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.1 $
 */
public class SetLineDashPattern extends OperatorProcessor
{

    /**
     * Set the line dash pattern.
     * @param operator The operator that is being executed.
     * @param arguments List
     *
     * @throws IOException If an error occurs while processing the font.
     */
    public void process(PDFOperator operator, List<COSBase> arguments) throws IOException
    {
        COSArray dashArray = (COSArray)arguments.get( 0 );
        int dashPhase = ((COSNumber)arguments.get( 1 )).intValue();
        PDLineDashPattern lineDash = new PDLineDashPattern( dashArray, dashPhase );
        context.getGraphicsState().setLineDashPattern( lineDash );
    }
}

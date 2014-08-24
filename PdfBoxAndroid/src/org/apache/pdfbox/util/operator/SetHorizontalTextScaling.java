package org.apache.pdfbox.util.operator;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.util.PDFOperator;

/**
 * <p>Structal modification of the PDFEngine class :
 * the long sequence of conditions in processOperator is remplaced by
 * this strategy pattern.</p>
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.5 $
 */

public class SetHorizontalTextScaling extends OperatorProcessor
{
    /**
     * Tz Set horizontal text scaling.
     * @param operator The operator that is being executed.
     * @param arguments List
     * @throws IOException If an error occurs while processing the font.
     */
    public void process(PDFOperator operator, List<COSBase> arguments) throws IOException
    {
        COSNumber scaling = (COSNumber)arguments.get(0);
        context.getGraphicsState().getTextState().setHorizontalScalingPercent( scaling.floatValue() );
    }
}

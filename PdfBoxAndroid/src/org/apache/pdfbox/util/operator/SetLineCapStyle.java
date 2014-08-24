package org.apache.pdfbox.util.operator;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.util.PDFOperator;

/**
 * Implementation of content stream operator for page drawer.
 *
 * @author <a href="mailto:andreas@lehmi.de>Andreas Lehmkühler</a>
 * @version $Revision: 1.0 $
 */
public class SetLineCapStyle extends OperatorProcessor
{

    /**
     * Set the line cap style.
     * @param operator The operator that is being executed.
     * @param arguments List
     *
     * @throws IOException If an error occurs while processing the font.
     */
    public void process(PDFOperator operator, List<COSBase> arguments) throws IOException
    {
        int lineCapStyle = ((COSNumber)arguments.get( 0 )).intValue();
        context.getGraphicsState().setLineCap( lineCapStyle );
    }
}

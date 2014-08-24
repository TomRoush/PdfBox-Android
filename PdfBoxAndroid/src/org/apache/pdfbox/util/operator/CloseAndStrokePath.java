package org.apache.pdfbox.util.operator;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.util.PDFOperator;

/**
 * Implementation of content stream operator for page drawer.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.2 $
 */
public class CloseAndStrokePath extends OperatorProcessor
{

    /**
     * s close and stroke the path.
     * @param operator The operator that is being executed.
     * @param arguments List
     *
     * @throws IOException If an error occurs while processing the font.
     */
    public void process(PDFOperator operator, List<COSBase> arguments) throws IOException
    {
        context.processOperator( "h", arguments );
        context.processOperator( "S", arguments );
    }
}

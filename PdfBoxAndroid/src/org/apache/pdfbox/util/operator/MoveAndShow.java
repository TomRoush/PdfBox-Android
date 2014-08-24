package org.apache.pdfbox.util.operator;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.util.PDFOperator;

/**
 * @author Huault : huault@free.fr
 * @version $Revision: 1.5 $
 */
public class MoveAndShow extends OperatorProcessor
{
    /**
     * ' Move to next line and show text.
     * @param arguments List
     * @param operator The operator that is being executed.
     * @throws IOException If there is an error processing the operator.
     */
    public void process(PDFOperator operator, List<COSBase> arguments) throws IOException
    {
        // Move to start of next text line, and show text
        //

        context.processOperator("T*", null);
        context.processOperator("Tj", arguments);
    }

}

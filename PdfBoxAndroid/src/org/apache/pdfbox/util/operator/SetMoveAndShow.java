package org.apache.pdfbox.util.operator;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.util.PDFOperator;

/**
 * @author Huault : huault@free.fr
 * @version $Revision: 1.6 $
 */

public class SetMoveAndShow extends OperatorProcessor
{
    /**
     * " Set word and character spacing, move to next line, and show text.
     * @param operator The operator that is being executed.
     * @param arguments List.
     * @throws IOException If there is an error processing the operator.
     */
    public void process(PDFOperator operator, List<COSBase> arguments) throws IOException
    {
        //Set word and character spacing, move to next line, and show text
        //
        context.processOperator("Tw", arguments.subList(0,1));
        context.processOperator("Tc", arguments.subList(1,2));
        context.processOperator("'", arguments.subList(2,3));
    }
}

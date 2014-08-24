package org.apache.pdfbox.util.operator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.util.PDFOperator;

/**
*
* @author Huault : huault@free.fr
* @version $Revision: 1.5 $
*/
public class NextLine extends OperatorProcessor
{
   /**
    * process : T* Move to start of next text line.
    * @param operator The operator that is being executed.
    * @param arguments List
    *
    * @throws IOException If there is an error during processing.
    */
   public void process(PDFOperator operator, List<COSBase> arguments) throws IOException
   {
       //move to start of next text line
       ArrayList<COSBase> args = new ArrayList<COSBase>();
       args.add(new COSFloat(0.0f));
       // this must be -leading instead of just leading as written in the
       // specification (p.369) the acrobat reader seems to implement it the same way
       args.add(new COSFloat(-1*context.getGraphicsState().getTextState().getLeading()));
       // use Td instead of repeating code
       context.processOperator("Td", args);

   }
}

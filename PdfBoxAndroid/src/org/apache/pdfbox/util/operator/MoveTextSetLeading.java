package org.apache.pdfbox.util.operator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.util.PDFOperator;

/**
*
* @author Huault : huault@free.fr
* @version $Revision: 1.5 $
*/
public class MoveTextSetLeading extends OperatorProcessor
{

   /**
    * process : TD Move text position and set leading.
    * @param operator The operator that is being executed.
    * @param arguments List
    *
    * @throws IOException If there is an error during processing.
    */
   public void process(PDFOperator operator, List<COSBase> arguments) throws IOException
   {
       //move text position and set leading
       COSNumber y = (COSNumber)arguments.get( 1 );

       ArrayList<COSBase> args = new ArrayList<COSBase>();
       args.add(new COSFloat(-1*y.floatValue()));
       context.processOperator("TL", args);
       context.processOperator("Td", arguments);

   }
}

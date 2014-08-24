package org.apache.pdfbox.util.operator;

import java.util.List;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.util.PDFOperator;

/**
*
* @author Huault : huault@free.fr
* @version $Revision: 1.4 $
*/
public class EndText extends OperatorProcessor
{

   /**
    * process : ET : End text object.
    * @param operator The operator that is being executed.
    * @param arguments List
    */
   public void process(PDFOperator operator, List<COSBase> arguments)
   {
       context.setTextMatrix( null);
       context.setTextLineMatrix( null);
   }

}

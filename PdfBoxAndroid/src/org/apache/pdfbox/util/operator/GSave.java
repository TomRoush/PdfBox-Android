package org.apache.pdfbox.util.operator;

import java.util.List;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.pdmodel.graphics.PDGraphicsState;
import org.apache.pdfbox.util.PDFOperator;

/**
*
* @author Huault : huault@free.fr
* @version $Revision: 1.4 $
*/

public class GSave extends OperatorProcessor
{
   /**
    * process : q : Save graphics state.
    * @param operator The operator that is being executed.
    * @param arguments List
    */
   public void process(PDFOperator operator, List<COSBase> arguments)
   {
       context.getGraphicsStack().push( (PDGraphicsState)context.getGraphicsState().clone() );
   }

}

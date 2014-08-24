package org.apache.pdfbox.util.operator;

import java.util.List;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.PDFOperator;

/**
*
* @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
* @author Huault : huault@free.fr
* @version $Revision: 1.5 $
*/
public class BeginText extends OperatorProcessor
{

   /**
    * process : BT : Begin text object.
    * @param operator The operator that is being executed.
    * @param arguments List
    */
   public void process(PDFOperator operator, List<COSBase> arguments)
   {
       context.setTextMatrix( new Matrix());
       context.setTextLineMatrix( new Matrix() );
   }
}

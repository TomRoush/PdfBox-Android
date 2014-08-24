package org.apache.pdfbox.util.operator;

import java.util.List;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.PDFOperator;

/**
*
* @author Huault : huault@free.fr
* @version $Revision: 1.4 $
*/
public class MoveText extends OperatorProcessor
{

   /**
    * process : Td : Move text position.
    * @param operator The operator that is being executed.
    * @param arguments List
    */
   public void process(PDFOperator operator, List<COSBase> arguments)
   {
       COSNumber x = (COSNumber)arguments.get( 0 );
       COSNumber y = (COSNumber)arguments.get( 1 );
       Matrix td = new Matrix();
       td.setValue( 2, 0, x.floatValue() );
       td.setValue( 2, 1, y.floatValue() );
       context.setTextLineMatrix( td.multiply( context.getTextLineMatrix() ) );
       context.setTextMatrix( context.getTextLineMatrix().copy() );
   }
}

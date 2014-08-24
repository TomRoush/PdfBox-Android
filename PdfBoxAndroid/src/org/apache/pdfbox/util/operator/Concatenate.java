package org.apache.pdfbox.util.operator;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.PDFOperator;

/**
*
* @author Huault : huault@free.fr
* @version $Revision: 1.5 $
*/

public class Concatenate extends OperatorProcessor
{

   /**
    * process : cm : Concatenate matrix to current transformation matrix.
    * @param operator The operator that is being executed.
    * @param arguments List
    * @throws IOException If there is an error processing the operator.
    */
   public void process(PDFOperator operator, List<COSBase> arguments) throws IOException
   {

       //concatenate matrix to current transformation matrix
       COSNumber a = (COSNumber) arguments.get(0);
       COSNumber b = (COSNumber) arguments.get(1);
       COSNumber c = (COSNumber) arguments.get(2);
       COSNumber d = (COSNumber) arguments.get(3);
       COSNumber e = (COSNumber) arguments.get(4);
       COSNumber f = (COSNumber) arguments.get(5);

       Matrix newMatrix = new Matrix();
       newMatrix.setValue(0, 0, a.floatValue());
       newMatrix.setValue(0, 1, b.floatValue());
       newMatrix.setValue(1, 0, c.floatValue());
       newMatrix.setValue(1, 1, d.floatValue());
       newMatrix.setValue(2, 0, e.floatValue());
       newMatrix.setValue(2, 1, f.floatValue());

       //this line has changed
       context.getGraphicsState().setCurrentTransformationMatrix(
               newMatrix.multiply( context.getGraphicsState().getCurrentTransformationMatrix() ) );


   }
}

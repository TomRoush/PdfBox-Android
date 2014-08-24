package org.apache.pdfbox.util.operator;

import java.util.List;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.util.PDFOperator;

/**
*
* @author Huault : huault@free.fr
* @version $Revision: 1.5 $
*/
public class SetCharSpacing extends OperatorProcessor
{
   /**
    * process : Tc Set character spacing.
    * @param operator The operator that is being executed.
    * @param arguments List
    */
   public void process(PDFOperator operator, List<COSBase> arguments)
   {
       //set character spacing
       if( arguments.size() > 0 )
       {
           //There are some documents which are incorrectly structured, and have
           //a wrong number of arguments to this, so we will assume the last argument
           //in the list
           Object charSpacing = arguments.get( arguments.size()-1 );
           if( charSpacing instanceof COSNumber )
           {
               COSNumber characterSpacing = (COSNumber)charSpacing;
               context.getGraphicsState().getTextState().setCharacterSpacing( characterSpacing.floatValue() );
           }
       }
   }
}

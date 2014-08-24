package org.apache.pdfbox.util.operator;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.PDFOperator;

/**
 * @author Huault : huault@free.fr
 * @version $Revision: 1.6 $
 */

public class ShowTextGlyph extends OperatorProcessor
{
    /**
     * TJ Show text, allowing individual glyph positioning.
     * @param operator The operator that is being executed.
     * @param arguments List
     * @throws IOException If there is an error processing this operator.
     */
    public void process(PDFOperator operator, List<COSBase> arguments) throws IOException
    {
        COSArray array = (COSArray)arguments.get( 0 );
        int arraySize = array.size();
        float fontsize = context.getGraphicsState().getTextState().getFontSize();
        float horizontalScaling = context.getGraphicsState().getTextState().getHorizontalScalingPercent()/100;
        for( int i=0; i<arraySize; i++ )
        {
            COSBase next = array.get( i );
            if( next instanceof COSNumber )
            {
                float adjustment = ((COSNumber)next).floatValue();
                Matrix adjMatrix = new Matrix();
                adjustment=-(adjustment/1000)*horizontalScaling*fontsize;
                // TODO vertical writing mode
                adjMatrix.setValue( 2, 0, adjustment );
                context.setTextMatrix( adjMatrix.multiply(context.getTextMatrix(), adjMatrix) );
            }
            else if( next instanceof COSString )
            {
                context.processEncodedText( ((COSString)next).getBytes() );
            }
            else
            {
                throw new IOException( "Unknown type in array for TJ operation:" + next );
            }
        }
    }

}

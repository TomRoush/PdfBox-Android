package org.apache.pdfbox.util.operator;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.util.PDFOperator;

/**
 * @author Huault : huault@free.fr
 * @version $Revision: 1.5 $
 */

public class SetTextFont extends OperatorProcessor
{
    /**
     * Tf selectfont Set text font and size.
     * @param operator The operator that is being executed.
     * @param arguments List
     * @throws IOException If an error occurs while processing the font.
     */
    public void process(PDFOperator operator, List<COSBase> arguments) throws IOException
    {
        //there are some documents that are incorrectly structured and
        //arguments are in the wrong spot, so we will silently ignore them
        //if there are no arguments
        if( arguments.size() >= 2 )
        {
            //set font and size
            COSName fontName = (COSName)arguments.get( 0 );
            float fontSize = ((COSNumber)arguments.get( 1 ) ).floatValue();
            context.getGraphicsState().getTextState().setFontSize( fontSize );

            context.getGraphicsState().getTextState().setFont( (PDFont)context.getFonts().get( fontName.getName() ) );
            if( context.getGraphicsState().getTextState().getFont() == null )
            {
                throw new IOException( "Error: Could not find font(" + fontName + ") in map=" + context.getFonts() );
            }
        }
    }

}

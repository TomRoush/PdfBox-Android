package org.apache.pdfbox.contentstream.operator.text;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.pdmodel.font.PDFont;

/**
 * Tf: Set text font and size.
 *
 * @author Laurent Huault
 */
public class SetFontAndSize extends OperatorProcessor
{
    @Override
    public void process(Operator operator, List<COSBase> arguments) throws IOException
    {
        // there are some documents that are incorrectly structured and
        // arguments are in the wrong spot, so we will silently ignore them
        // if there are no arguments
        if( arguments.size() >= 2 )
        {
            // set font and size
        	COSName fontName = (COSName)arguments.get(0);
        	float fontSize = ((COSNumber)arguments.get(1)).floatValue();
        	context.getGraphicsState().getTextState().setFontSize(fontSize);
        	PDFont font = context.getResources().getFont(fontName);
        	context.getGraphicsState().getTextState().setFont(font);
        }
    }

    @Override
    public String getName()
    {
        return "Tf";
    }
}

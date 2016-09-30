package org.apache.pdfbox.contentstream.operator.text;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSString;

/**
 * Tj: Show text.
 *
 * @author Laurent Huault
 */
public class ShowText extends OperatorProcessor
{
    @Override
    public void process(Operator operator, List<COSBase> arguments) throws IOException
    {
    	if (arguments.size() < 1)
    	{
    		// ignore ( )Tj
    		return;
    	}
        COSString string = (COSString)arguments.get( 0 );
        context.showTextString(string.getBytes());
    }

    @Override
    public String getName()
    {
        return "Tj";
    }
}

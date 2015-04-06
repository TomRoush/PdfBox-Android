package org.apache.pdfbox.contentstream.operator.markedcontent;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.text.PDFMarkedContentExtractor;

/**
 * BMC : Begins a marked-content sequence.
 *
 * @author Johannes Koch
 */
public class BeginMarkedContentSequence extends OperatorProcessor
{
    @Override
    public void process(Operator operator, List<COSBase> arguments) throws IOException
    {
        COSName tag = null;
        for (COSBase argument : arguments)
        {
            if (argument instanceof COSName)
            {
                tag = (COSName) argument;
            }
        }
        if (this.context instanceof PDFMarkedContentExtractor)
        {
            ((PDFMarkedContentExtractor) this.context).beginMarkedContentSequence(tag, null);
        }
    }

    @Override
    public String getName()
    {
        return "BMC";
    }
}

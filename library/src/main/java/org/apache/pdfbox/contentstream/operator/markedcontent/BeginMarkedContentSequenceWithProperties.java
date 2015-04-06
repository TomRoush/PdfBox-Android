package org.apache.pdfbox.contentstream.operator.markedcontent;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.text.PDFMarkedContentExtractor;

/**
 * BDC : Begins a marked-content sequence with property list.
 *
 * @author Johannes Koch
 */
public class BeginMarkedContentSequenceWithProperties extends OperatorProcessor
{
    @Override
    public void process(Operator operator, List<COSBase> arguments) throws IOException
    {
        COSName tag = null;
        COSDictionary properties = null;
        for (COSBase argument : arguments)
        {
            if (argument instanceof COSName)
            {
                tag = (COSName) argument;
            }
            else if (argument instanceof COSDictionary)
            {
                properties = (COSDictionary) argument;
            }
        }
        if (this.context instanceof PDFMarkedContentExtractor)
        {
            ((PDFMarkedContentExtractor) this.context).beginMarkedContentSequence(tag, properties);
        }
    }

    @Override
    public String getName()
    {
        return "BDC";
    }
}

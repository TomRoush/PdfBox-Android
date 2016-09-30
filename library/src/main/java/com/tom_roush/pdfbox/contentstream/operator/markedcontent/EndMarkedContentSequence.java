package org.apache.pdfbox.contentstream.operator.markedcontent;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.text.PDFMarkedContentExtractor;

/**
 * EMC : Ends a marked-content sequence begun by BMC or BDC.
 *
 * @author Johannes Koch
 */
public class EndMarkedContentSequence extends OperatorProcessor
{
    @Override
    public void process(Operator operator, List<COSBase> arguments) throws IOException
    {
        if (this.context instanceof PDFMarkedContentExtractor)
        {
            ((PDFMarkedContentExtractor) this.context).endMarkedContentSequence();
        }
    }

    @Override
    public String getName()
    {
        return "EMC";
    }
}

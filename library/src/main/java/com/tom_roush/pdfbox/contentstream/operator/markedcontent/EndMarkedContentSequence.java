package com.tom_roush.pdfbox.contentstream.operator.markedcontent;

import java.io.IOException;
import java.util.List;

import com.tom_roush.pdfbox.contentstream.operator.Operator;
import com.tom_roush.pdfbox.contentstream.operator.OperatorProcessor;
import com.tom_roush.pdfbox.cos.COSBase;
import com.tom_roush.pdfbox.text.PDFMarkedContentExtractor;

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

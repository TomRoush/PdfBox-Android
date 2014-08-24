package org.apache.pdfbox.util.operator;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.util.PDFMarkedContentExtractor;
import org.apache.pdfbox.util.PDFOperator;

/**
 * BMC : Begins a marked-content sequence.
 * @author koch
 * @version $Revision$
 *
 */
public class BeginMarkedContentSequence extends OperatorProcessor
{

    /**
     * {@inheritDoc} 
     */
    @Override
    public void process(PDFOperator operator, List<COSBase> arguments)
        throws IOException
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

}

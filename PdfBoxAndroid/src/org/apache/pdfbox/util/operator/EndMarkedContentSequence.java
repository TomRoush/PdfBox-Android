package org.apache.pdfbox.util.operator;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.util.PDFMarkedContentExtractor;
import org.apache.pdfbox.util.PDFOperator;

/**
 * EMC : Ends a marked-content sequence begun by BMC or BDC.
 * @author koch
 * @version $Revision: $
 */
public class EndMarkedContentSequence extends OperatorProcessor
{

    /**
     * {@inheritDoc}
     */
    public void process(PDFOperator operator, List<COSBase> arguments)
        throws IOException
    {
        if (this.context instanceof PDFMarkedContentExtractor)
        {
            ((PDFMarkedContentExtractor) this.context).endMarkedContentSequence();
        }
    }

}

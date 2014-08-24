package org.apache.pdfbox.util.operator;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.util.PDFMarkedContentExtractor;
import org.apache.pdfbox.util.PDFOperator;

/**
 * BDC : Begins a marked-content sequence with property list.
 *
 * @author koch
 * @version $Revision$
 */
public class BeginMarkedContentSequenceWithProperties extends OperatorProcessor
{

    /**
     * {@inheritDoc}
     */
    public void process(PDFOperator operator, List<COSBase> arguments)
        throws IOException
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

}

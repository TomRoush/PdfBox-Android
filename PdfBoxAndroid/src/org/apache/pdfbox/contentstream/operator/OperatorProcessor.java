package org.apache.pdfbox.contentstream.operator;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.contentstream.PDFStreamEngine;
import java.util.List;
import java.io.IOException;

/**
 * Processes a PDF operator.
 *
 * @author Laurent Huault
 */
public abstract class OperatorProcessor
{
    /** The processing context. */
    protected PDFStreamEngine context;

    /**
     * Creates a new OperatorProcessor.
     */
    protected OperatorProcessor()
    {
    }

    /**
     * Returns the processing context.
     * @return the processing context
     */
    protected final PDFStreamEngine getContext()
    {
        return context;
    }

    /**
     * Sets the processing context.
     * @param context the processing context.
     */
    public void setContext(PDFStreamEngine context)
    {
        this.context = context;
    }

    /**
     * Process the operator.
     * @param operator the operator to process
     * @param operands the operands to use when processing
     * @throws IOException if the operator cannot be processed
     */
    public abstract void process(Operator operator, List<COSBase> operands) throws IOException;

    /**
     * Returns the name of this operator, e.g. "BI".
     */
    public abstract String getName();
}

package org.apache.pdfbox.util.operator;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.util.PDFOperator;
import org.apache.pdfbox.util.PDFStreamEngine;

/**
 * @author Huault : huault@free.fr
 * @version $Revision: 1.3 $
 */
public abstract class OperatorProcessor
{

    /**
     * The stream engine processing context.
     */
    protected PDFStreamEngine context = null;

    /**
     * Constructor.
     *
     */
    protected OperatorProcessor()
    {
    }

    /**
     * Get the context for processing.
     *
     * @return The processing context.
     */
    protected PDFStreamEngine getContext()
    {
        return context;
    }

    /**
     * Set the processing context.
     *
     * @param ctx The context for processing.
     */
    public void setContext(PDFStreamEngine ctx)
    {
        context = ctx;
    }

    /**
     * process the operator.
     * @param operator The operator that is being processed.
     * @param arguments arguments needed by this operator.
     *
     * @throws IOException If there is an error processing the operator.
     */
    public abstract void process(PDFOperator operator, List<COSBase> arguments) throws IOException;
}

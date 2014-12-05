package org.apache.pdfbox.contentstream.operator;

import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.cos.COSBase;

/**
 * Processes a PDF operator.
 *
 * @author Laurent Huault
 */
public abstract class OperatorProcessor
{
	/**
	 * Log instance.
	 */
	private static final Log LOG = LogFactory.getLog(OperatorProcessor.class);
	
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
    protected PDFStreamEngine getContext()
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
    
    protected boolean checkArgumentSize(List<COSBase> arguments, int expectedSize)
    {
    	if (arguments.size() < expectedSize)
    	{
    		LOG.warn("'" + getName() + "' operator must have " + expectedSize
    				+ " parameters, but has " + arguments.size());
    		return false;
    	}
    	return true;
    }
}

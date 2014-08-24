package org.apache.pdfbox.util.operator;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.pdmodel.graphics.PDGraphicsState;
import org.apache.pdfbox.util.PDFOperator;

/**
 * Process the Q operator.
 * 
 * @author Huault : huault@free.fr
 * 
 */
public class GRestore extends OperatorProcessor
{
	
    /**
     * Log instance.
     */
    private static final Log LOG = LogFactory.getLog(GRestore.class);

    /**
     * {@inheritDoc}
     */
    public void process(PDFOperator operator, List<COSBase> arguments)
    {
    	if (context.getGraphicsStack().size() > 0)
    	{
    		context.setGraphicsState( (PDGraphicsState)context.getGraphicsStack().pop() );
    	}
    	else
    	{
    		// this shouldn't happen but it does, see PDFBOX-161
    		// TODO make this self healing mechanism optional for preflight??
    		LOG.debug("GRestore: no graphics state left to be restored.");
    	}
    }
}

package org.apache.pdfbox.contentstream.operator.state;

import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.cos.COSBase;

/**
 * Q: Restore the graphics state.
 * 
 * @author Laurent Huault
 */
public class Restore extends OperatorProcessor
{
    private static final Log LOG = LogFactory.getLog(Restore.class);

    @Override
    public void process(Operator operator, List<COSBase> arguments) throws IOException
    {
        if (context.getGraphicsStackSize() > 1)
        {
            context.restoreGraphicsState();
        }
        else
        {
            // this shouldn't happen but it does, see PDFBOX-161
        	throw new EmptyGraphicsStackException();
        }
    }

    @Override
    public String getName()
    {
        return "Q";
    }
}

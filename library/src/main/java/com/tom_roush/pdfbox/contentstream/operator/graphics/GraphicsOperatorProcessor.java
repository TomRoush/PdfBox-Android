package com.tom_roush.pdfbox.contentstream.operator.graphics;

import com.tom_roush.pdfbox.contentstream.PDFGraphicsStreamEngine;
import com.tom_roush.pdfbox.contentstream.PDFStreamEngine;
import com.tom_roush.pdfbox.contentstream.operator.OperatorProcessor;

/**
 * Base class for graphics operators.
 *
 * @author John Hewson
 */
public abstract class GraphicsOperatorProcessor extends OperatorProcessor
{
    /** The processing context. */
    protected PDFGraphicsStreamEngine context;

    @Override
    public void setContext(PDFStreamEngine context)
    {
        super.setContext(context);
        this.context = (PDFGraphicsStreamEngine)context;
    }
}

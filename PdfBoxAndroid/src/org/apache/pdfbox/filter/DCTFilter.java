package org.apache.pdfbox.filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSDictionary;

/**
 * This is the used for the DCTDecode filter.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.10 $
 */
public class DCTFilter implements Filter
{

    /**
     * Log instance.
     */
    private static final Log log = LogFactory.getLog(DCTFilter.class);

    /**
     * {@inheritDoc}
     */
    public void decode( InputStream compressedData, OutputStream result, COSDictionary options, int filterIndex ) 
        throws IOException
    {
        log.warn( "DCTFilter.decode is not implemented yet, skipping this stream." );
    }

     /**
     * {@inheritDoc}
     */
    public void encode( InputStream rawData, OutputStream result, COSDictionary options, int filterIndex ) 
        throws IOException
    {
        log.warn( "DCTFilter.encode is not implemented yet, skipping this stream." );
    }
}

package org.apache.pdfbox.filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.pdfbox.cos.COSDictionary;

/**
 * The IdentityFilter filter just passes the data through without any modifications.
 * This is defined in section 7.6.5 of the PDF 1.7 spec and also stated in table
 * 26.
 * 
 * @author adam.nichols
 */
public class IdentityFilter implements Filter
{
    private static final int BUFFER_SIZE = 1024;
    
    /**
     * {@inheritDoc}
     */
    public void decode( InputStream compressedData, OutputStream result, COSDictionary options, int filterIndex ) 
        throws IOException
    {
        byte[] buffer = new byte[BUFFER_SIZE];
        int amountRead = 0;
        while( (amountRead = compressedData.read( buffer, 0, BUFFER_SIZE )) != -1 )
        {
            result.write( buffer, 0, amountRead );
        }
        result.flush();
    }
    
    /**
     * {@inheritDoc}
     */
    public void encode( InputStream rawData, OutputStream result, COSDictionary options, int filterIndex ) 
        throws IOException
    {
        byte[] buffer = new byte[BUFFER_SIZE];
        int amountRead = 0;
        while( (amountRead = rawData.read( buffer, 0, BUFFER_SIZE )) != -1 )
        {
            result.write( buffer, 0, amountRead );
        }
        result.flush();
    }
}

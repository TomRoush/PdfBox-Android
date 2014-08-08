package org.apache.pdfbox.filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.io.ASCII85InputStream;
import org.apache.pdfbox.io.ASCII85OutputStream;

/**
 * This is the used for the ASCIIHexDecode filter.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.8 $
 */
public class ASCII85Filter implements Filter
{
    /**
     * {@inheritDoc}
     */
    public void decode( InputStream compressedData, OutputStream result, COSDictionary options, int filterIndex ) 
        throws IOException
    {
        ASCII85InputStream is = null;
        try
        {
            is = new ASCII85InputStream(compressedData);
            byte[] buffer = new byte[1024];
            int amountRead = 0;
            while( (amountRead = is.read( buffer, 0, 1024) ) != -1 )
            {
                result.write(buffer, 0, amountRead);
            }
            result.flush();
        }
        finally
        {
            if( is != null )
            {
                is.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void encode( InputStream rawData, OutputStream result, COSDictionary options, int filterIndex ) 
        throws IOException
    {
        ASCII85OutputStream os = new ASCII85OutputStream(result);
        byte[] buffer = new byte[1024];
        int amountRead = 0;
        while( (amountRead = rawData.read( buffer, 0, 1024 )) != -1 )
        {
            os.write( buffer, 0, amountRead );
        }
        os.close();
        result.flush();
    }
}

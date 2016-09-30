package com.tom_roush.pdfbox.filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.io.IOUtils;

/**
 * Decodes data encoded in an ASCII base-85 representation, reproducing the original binary data.
 * @author Ben Litchfield
 */
final class ASCII85Filter extends Filter
{
    @Override
    public DecodeResult decode(InputStream encoded, OutputStream decoded,
                                         COSDictionary parameters, int index) throws IOException
    {
        ASCII85InputStream is = null;
        try
        {
            is = new ASCII85InputStream(encoded);
            byte[] buffer = new byte[1024];
            int amountRead;
            while((amountRead = is.read(buffer, 0, 1024))!= -1)
            {
                decoded.write(buffer, 0, amountRead);
            }
            decoded.flush();
        }
        finally
        {
            IOUtils.closeQuietly(is);
        }
        return new DecodeResult(parameters);
    }

    @Override
    protected void encode(InputStream input, OutputStream encoded, COSDictionary parameters)
        throws IOException
    {
        ASCII85OutputStream os = new ASCII85OutputStream(encoded);
        byte[] buffer = new byte[1024];
        int amountRead;
        while((amountRead = input.read(buffer, 0, 1024))!= -1)
        {
            os.write(buffer, 0, amountRead);
        }
        os.close();
        encoded.flush();
    }
}

package org.apache.pdfbox.filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.pdfbox.cos.COSDictionary;

import android.util.Log;

/**
 * Decompresses data encoded using a byte-oriented run-length encoding algorithm,
 * reproducing the original text or binary data
 *
 * @author Ben Litchfield
 */
final class RunLengthDecodeFilter extends Filter
{
    private static final int RUN_LENGTH_EOD = 128;

    @Override
    public DecodeResult decode(InputStream encoded, OutputStream decoded,
                                         COSDictionary parameters, int index) throws IOException
    {
        int dupAmount;
        byte[] buffer = new byte[128];
        while ((dupAmount = encoded.read()) != -1 && dupAmount != RUN_LENGTH_EOD)
        {
            if (dupAmount <= 127)
            {
                int amountToCopy = dupAmount + 1;
                int compressedRead;
                while(amountToCopy > 0)
                {
                    compressedRead = encoded.read(buffer, 0, amountToCopy);
                    decoded.write(buffer, 0, compressedRead);
                    amountToCopy -= compressedRead;
                }
            }
            else
            {
                int dupByte = encoded.read();
                for (int i = 0; i < 257 - dupAmount; i++)
                {
                    decoded.write(dupByte);
                }
            }
        }
        return new DecodeResult(parameters);
    }

    @Override
    protected void encode(InputStream input, OutputStream encoded, COSDictionary parameters)
            throws IOException
    {
    	Log.w("PdfBoxAndroid", "RunLengthDecodeFilter.encode is not implemented yet, skipping this stream.");
    }
}

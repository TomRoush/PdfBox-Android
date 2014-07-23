package org.apache.pdfbox.filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.pdfbox.cos.COSDictionary;

/**
 * This is the interface that will be used to apply filters to a byte stream.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.7 $
 */
public interface Filter
{
    /**
     * This will decode some compressed data.
     *
     * @param compressedData The compressed byte stream.
     * @param result The place to write the uncompressed byte stream.
     * @param options The options to use to encode the data.
     * @param filterIndex The index to the filter being decoded.
     *
     * @throws IOException If there is an error decompressing the stream.
     */
    public void decode( InputStream compressedData, OutputStream result, COSDictionary options, int filterIndex ) 
        throws IOException;

    /**
     * This will encode some data.
     *
     * @param rawData The raw data to encode.
     * @param result The place to write to encoded results to.
     * @param options The options to use to encode the data.
     * @param filterIndex The index to the filter being encoded.
     *
     * @throws IOException If there is an error compressing the stream.
     */
    public void encode( InputStream rawData, OutputStream result, COSDictionary options, int filterIndex ) 
        throws IOException;
}

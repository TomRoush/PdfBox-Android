package com.tom_roush.pdfbox.filter;

import com.tom_roush.pdfbox.cos.COSName;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory for Filter classes.
 *
 * @author Ben Litchfield
 */
public final class FilterFactory
{
    /**
     * Singleton instance.
     */
    public static final FilterFactory INSTANCE = new FilterFactory();

    private final Map<COSName, Filter> filters = new HashMap<COSName, Filter>();

    private FilterFactory()
    {
        Filter flate = new FlateFilter();
        Filter dct = new DCTFilter();
        Filter ccittFax = new CCITTFaxFilter();
        Filter lzw = new LZWFilter();
        Filter asciiHex = new ASCIIHexFilter();
        Filter ascii85 = new ASCII85Filter();
        Filter runLength = new RunLengthDecodeFilter();
        Filter crypt = new CryptFilter();
//        Filter jpx = new JPXFilter();
//        Filter jbig2 = new JBIG2Filter();TODO: PdfBox-Android

        filters.put(COSName.FLATE_DECODE, flate);
        filters.put(COSName.FLATE_DECODE_ABBREVIATION, flate);
        filters.put(COSName.DCT_DECODE, dct);
        filters.put(COSName.DCT_DECODE_ABBREVIATION, dct);
        filters.put(COSName.CCITTFAX_DECODE, ccittFax);
        filters.put(COSName.CCITTFAX_DECODE_ABBREVIATION, ccittFax);
        filters.put(COSName.LZW_DECODE, lzw);
        filters.put(COSName.LZW_DECODE_ABBREVIATION, lzw);
        filters.put(COSName.ASCII_HEX_DECODE, asciiHex);
        filters.put(COSName.ASCII_HEX_DECODE_ABBREVIATION, asciiHex);
        filters.put(COSName.ASCII85_DECODE, ascii85);
        filters.put(COSName.ASCII85_DECODE_ABBREVIATION, ascii85);
        filters.put(COSName.RUN_LENGTH_DECODE, runLength);
        filters.put(COSName.RUN_LENGTH_DECODE_ABBREVIATION, runLength);
        filters.put(COSName.CRYPT, crypt);
//        filters.put(COSName.JPX_DECODE, jpx);
//        filters.put(COSName.JBIG2_DECODE, jbig2);TODO: PdfBox-Android
    }

    /**
     * Returns a filter instance given its name as a string.
     * @param filterName the name of the filter to retrieve
     * @return the filter that matches the name
     * @throws IOException if the filter name was invalid
     */
    public Filter getFilter(String filterName) throws IOException
    {
        return getFilter(COSName.getPDFName(filterName));
    }

    /**
     * Returns a filter instance given its COSName.
     * @param filterName the name of the filter to retrieve
     * @return the filter that matches the name
     * @throws IOException if the filter name was invalid
     */
    public Filter getFilter(COSName filterName) throws IOException
    {
        Filter filter = filters.get(filterName);
        if (filter == null)
        {
            throw new IOException("Invalid filter: " + filterName);
        }
        return filter;
    }

    // returns all available filters, for testing
    Collection<Filter> getAllFilters()
    {
        return filters.values();
    }
}

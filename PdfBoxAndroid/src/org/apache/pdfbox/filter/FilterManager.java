package org.apache.pdfbox.filter;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.pdfbox.cos.COSName;

/**
 * This will contain manage all the different types of filters that are available.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.13 $
 */
public class FilterManager
{
    private Map<COSName, Filter> filters = new HashMap<COSName, Filter>();

    /**
     * Constructor.
     */
    public FilterManager()
    {
        Filter flateFilter = new FlateFilter();
//        Filter dctFilter = new DCTFilter();
        Filter ccittFaxFilter = new CCITTFaxDecodeFilter();
//        Filter lzwFilter = new LZWFilter();
//        Filter asciiHexFilter = new ASCIIHexFilter();
//        Filter ascii85Filter = new ASCII85Filter();
//        Filter runLengthFilter = new RunLengthDecodeFilter();
//        Filter cryptFilter = new CryptFilter();
//        Filter jpxFilter = new JPXFilter();
//        Filter jbig2Filter = new JBIG2Filter();
        
        addFilter( COSName.FLATE_DECODE, flateFilter );
        addFilter( COSName.FLATE_DECODE_ABBREVIATION, flateFilter );
//        addFilter( COSName.DCT_DECODE, dctFilter );
//        addFilter( COSName.DCT_DECODE_ABBREVIATION, dctFilter );
        addFilter( COSName.CCITTFAX_DECODE, ccittFaxFilter );
        addFilter( COSName.CCITTFAX_DECODE_ABBREVIATION, ccittFaxFilter );
//        addFilter( COSName.LZW_DECODE, lzwFilter );
//        addFilter( COSName.LZW_DECODE_ABBREVIATION, lzwFilter );
//        addFilter( COSName.ASCII_HEX_DECODE, asciiHexFilter );
//        addFilter( COSName.ASCII_HEX_DECODE_ABBREVIATION, asciiHexFilter );
//        addFilter( COSName.ASCII85_DECODE, ascii85Filter );
//        addFilter( COSName.ASCII85_DECODE_ABBREVIATION, ascii85Filter );
//        addFilter( COSName.RUN_LENGTH_DECODE, runLengthFilter );
//        addFilter( COSName.RUN_LENGTH_DECODE_ABBREVIATION, runLengthFilter );
//        addFilter( COSName.CRYPT, cryptFilter );
//        addFilter( COSName.JPX_DECODE, jpxFilter );
//        addFilter( COSName.JBIG2_DECODE, jbig2Filter );
        
    }

    /**
     * This will get all of the filters that are available in the system.
     *
     * @return All available filters in the system.
     */
    public Collection<Filter> getFilters()
    {
        return filters.values();
    }

    /**
     * This will add an available filter.
     *
     * @param filterName The name of the filter.
     * @param filter The filter to use.
     */
    public void addFilter( COSName filterName, Filter filter )
    {
        filters.put( filterName, filter );
    }

    /**
     * This will get a filter by name.
     *
     * @param filterName The name of the filter to retrieve.
     *
     * @return The filter that matches the name.
     *
     * @throws IOException If the filter could not be found.
     */
    public Filter getFilter( COSName filterName ) throws IOException
    {
        Filter filter = (Filter)filters.get( filterName );
        if( filter == null )
        {
            throw new IOException( "Unknown stream filter:" + filterName );
        }

        return filter;
    }

    /**
     * This will get a filter by name.
     *
     * @param filterName The name of the filter to retrieve.
     *
     * @return The filter that matches the name.
     *
     * @throws IOException If the filter could not be found.
     */
    public Filter getFilter( String filterName ) throws IOException
    {
        return getFilter( COSName.getPDFName( filterName ) );
    }
}

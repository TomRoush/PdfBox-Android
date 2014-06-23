package org.apache.pdfboxandroid.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.pdfbox.filter.FlateFilter;
import org.apache.pdfboxandroid.cos.COSName;

/**
 * This will contain manage all the different types of filters that are available.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.13 $
 */
public class FilterManager {
	private Map<COSName, Filter> filters = new HashMap<COSName, Filter>();
	
	/**
     * Constructor.
     */
    public FilterManager()
    {
    	Filter flateFilter = new FlateFilter();
    	
    	addFilter( COSName.FLATE_DECODE, flateFilter );
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
}

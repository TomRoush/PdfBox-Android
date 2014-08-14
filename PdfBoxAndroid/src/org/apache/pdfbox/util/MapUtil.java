package org.apache.pdfbox.util;

import java.util.Map;

/**
 * This class with handle some simple Map operations.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.2 $
 */
public class MapUtil
{
    private MapUtil()
    {
        //utility class
    }

    /**
     * Generate a unique key for the map based on a prefix.
     *
     * @param map The map to look for existing keys.
     * @param prefix The prefix to use when generating the key.
     * @return The new unique key that does not currently exist in the map.
     */
    public static final String getNextUniqueKey( Map<String,?> map, String prefix )
    {
        int counter = 0;
        while( map != null && map.get( prefix+counter ) != null )
        {
            counter++;
        }
        return prefix+counter;
    }
}

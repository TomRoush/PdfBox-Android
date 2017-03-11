package com.tom_roush.pdfbox.pdmodel.font;

import com.tom_roush.fontbox.cmap.CMap;
import com.tom_roush.fontbox.cmap.CMapParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * CMap resource loader and cache.
 */
final class CMapManager
{
    static Map<String, CMap> cMapCache =
            Collections.synchronizedMap(new HashMap<String, CMap>());
    
    private CMapManager()
    {
    }

    /**
     * Fetches the predefined CMap from disk (or cache).
     *
     * @param cMapName CMap name
     */
    public static CMap getPredefinedCMap(String cMapName) throws IOException
    {
        CMap cmap = cMapCache.get(cMapName);
        if (cmap != null)
        {
            return cmap;
        }

        CMapParser parser = new CMapParser();
        CMap targetCmap = parser.parsePredefined(cMapName);

        // limit the cache to predefined CMaps
        cMapCache.put(targetCmap.getName(), targetCmap);
        return targetCmap;
    }

    /**
     * Parse the given CMap.
     *
     * @param cMapStream the CMap to be read
     * @return the parsed CMap
     */
    public static CMap parseCMap(InputStream cMapStream) throws IOException
    {
        CMap targetCmap = null;
        if (cMapStream != null)
        {
            CMapParser parser = new CMapParser();
            targetCmap = parser.parse(cMapStream);
        }
        return targetCmap;
    }
}

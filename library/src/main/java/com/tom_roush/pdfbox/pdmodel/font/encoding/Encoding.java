package com.tom_roush.pdfbox.pdmodel.font.encoding;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.pdmodel.common.COSObjectable;

/**
 * A PostScript encoding vector, maps character codes to glyph names.
 * 
 * @author Ben Litchfield
 */
public abstract class Encoding implements COSObjectable
{
    /**
     * This will get an encoding by name. May return null.
     *
     * @param name The name of the encoding to get.
     * @return The encoding that matches the name.
     */
    public static Encoding getInstance(COSName name)
    {
        if (COSName.STANDARD_ENCODING.equals(name))
        {
            return StandardEncoding.INSTANCE;
        }
        else if (COSName.WIN_ANSI_ENCODING.equals(name))
        {
            return WinAnsiEncoding.INSTANCE;
        }
        else if (COSName.MAC_ROMAN_ENCODING.equals(name))
        {
            return MacRomanEncoding.INSTANCE;
        }
        else
        {
            return null;
        }
    }

    protected final Map<Integer, String> codeToName = new HashMap<Integer, String>();
    protected final Set<String> names = new HashSet<String>();

    /**
     * Returns an unmodifiable view of the Code2Name mapping.
     * 
     * @return the Code2Name map
     */
    public Map<Integer, String> getCodeToNameMap()
    {
        return Collections.unmodifiableMap(codeToName);
    }

    /**
     * This will add a character encoding.
     * 
     * @param code character code
     * @param name PostScript glyph name
     */
    protected void add(int code, String name)
    {
        codeToName.put(code, name);
        names.add(name);
    }

    /**
     * Determines if the encoding has a mapping for the given name value.
     * 
     * @param name PostScript glyph name
     */
    public boolean contains(String name)
    {
        return names.contains(name);
    }

    /**
     * Determines if the encoding has a mapping for the given code value.
     * 
     * @param code character code
     */
    public boolean contains(int code)
    {
        return codeToName.containsKey(code);
    }

    /**
     * This will take a character code and get the name from the code.
     * 
     * @param code character code
     * @return PostScript glyph name
     */
    public String getName(int code)
    {
       String name = codeToName.get(code);
       if (name != null)
       {
          return name;
       }
       return ".notdef";
    }
}

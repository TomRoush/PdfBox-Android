package org.apache.pdfbox.pdmodel.font;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.fontbox.cff.CFFFont;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.fontbox.type1.Type1Font;

/**
 * External font service provider interface. Implementations are expected to be thread safe.
 *
 * @author John Hewson
 */
public abstract class FontProvider
{
    /**
     * Returns a TrueType which corresponds to the given PostScript name. If there is no
     * suitable font, then this method will return null.
     *
     * @param postScriptName PostScript font name
     */
    public abstract TrueTypeFont getTrueTypeFont(String postScriptName);

    /**
     * Returns a CFF font which corresponds to the given PostScript name. If there is no
     * suitable font, then this method will return null.
     *
     * @param postScriptName PostScript font name
     */
    public abstract CFFFont getCFFFont(String postScriptName);

    /**
     * Returns a Type 1 which corresponds to the given PostScript name. If there is no
     * suitable font, then this method will return null.
     *
     * @param postScriptName PostScript font name
     */
    public abstract Type1Font getType1Font(String postScriptName);

    /**
     * Returns a string containing debugging information. This will be written to the log if no
     * suitable fonts are found and no fallback fonts are available. May be null.
     */
    public abstract String toDebugString();
    
    /**
     * Returns the font names for a given font. This allows substitution based on the PostScript
     * name of the external font, instead of just the BaseName in the PDF.
     */
    protected final Set<String> getNames(TrueTypeFont font) throws IOException
    {
        return getPostScriptNames(font.getName());
        // could add format-specific names here if needed
    }

    /**
     * Returns the font names for a given font. This allows substitution based on the PostScript
     * name of the external font, instead of just the BaseName in the PDF.
     */
    protected final Set<String> getNames(Type1Font font) throws IOException
    {
        return getPostScriptNames(font.getName());
        // could add format-specific names here if needed
    }

    /**
     * Returns the font names for a given font. This allows substitution based on the PostScript
     * name of the external font, instead of just the BaseName in the PDF.
     */
    protected final Set<String> getNames(CFFFont font) throws IOException
    {
        return getPostScriptNames(font.getName());
        // could add format-specific names here if needed
    }

    /**
     * Returns a list of alternative names for the given PostScript name.
     */
    private Set<String> getPostScriptNames(String postScriptName) throws IOException
    {
        Set<String> names = new HashSet<String>();

        // built-in PostScript name
        names.add(postScriptName);

        // remove hyphens (e.g. Arial-Black -> ArialBlack)
        names.add(postScriptName.replaceAll("-", ""));

        return names;
    }
}
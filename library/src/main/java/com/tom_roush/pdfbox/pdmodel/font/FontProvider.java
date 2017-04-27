package com.tom_roush.pdfbox.pdmodel.font;

import java.util.List;

/**
 * External font service provider interface.
 *
 * @author John Hewson
 */
public abstract class FontProvider
{
    /**
     * Returns a string containing debugging information. This will be written to the log if no
     * suitable fonts are found and no fallback fonts are available. May be null.
     */
    public abstract String toDebugString();

    /**
     * Returns a list of information about fonts on the system.
     */
    public abstract List<? extends FontInfo> getFontInfo();
}
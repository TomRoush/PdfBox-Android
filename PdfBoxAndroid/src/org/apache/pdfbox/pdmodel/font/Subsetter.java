package org.apache.pdfbox.pdmodel.font;

import java.io.IOException;
import java.util.Set;

/**
 * Interface for a font subsetter.
 */
interface Subsetter
{
    public void subset(Set<Integer> codePoints) throws IOException;
}
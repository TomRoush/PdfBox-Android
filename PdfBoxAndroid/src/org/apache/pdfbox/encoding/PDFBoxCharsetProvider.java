package org.apache.pdfbox.encoding;

import java.nio.charset.Charset;
import java.nio.charset.spi.CharsetProvider;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * {@link CharsetProvider} implementation for publishing PDFBox's encodings.
 * @version $Revision$
 */
public class PDFBoxCharsetProvider extends CharsetProvider
{

    private final Set<Charset> available = new java.util.HashSet<Charset>();
    private final Map<String, Charset> map = new java.util.HashMap<String, Charset>();

    /**
     * Constructor.
     */
    public PDFBoxCharsetProvider()
    {
        available.add(PDFDocEncodingCharset.INSTANCE);
        for (Charset cs : available)
        {
            map.put(cs.name(), cs);
            for (String alias : cs.aliases())
            {
                map.put(alias, cs);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public Iterator<Charset> charsets()
    {
        return Collections.unmodifiableSet(available).iterator();
    }

    /** {@inheritDoc} */
    @Override
    public Charset charsetForName(String charsetName)
    {
        return map.get(charsetName);
    }

}

package org.apache.pdfbox.encoding;

import java.util.Iterator;

import org.apache.fontbox.afm.CharMetric;
import org.apache.fontbox.afm.FontMetric;
import org.apache.pdfbox.cos.COSBase;

/**
 * This will handle the encoding from an AFM font.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.8 $
 */
public class AFMEncoding extends Encoding
{
    private FontMetric metric = null;

    /**
     * Constructor.
     *
     * @param fontInfo The font metric information.
     */
    public AFMEncoding( FontMetric fontInfo )
    {
        metric = fontInfo;
        Iterator<CharMetric> characters = metric.getCharMetrics().iterator();
        while( characters.hasNext() )
        {
            CharMetric nextMetric = (CharMetric)characters.next();
            addCharacterEncoding( nextMetric.getCharacterCode(), nextMetric.getName() );
        }
    }

    /**
     * Convert this standard java object to a COS object.
     *
     * @return The cos object that matches this Java object.
     */
    public COSBase getCOSObject()
    {
        return null;
    }
}

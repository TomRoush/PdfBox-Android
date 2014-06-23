package org.apache.pdfboxandroid.pdmodel.font;

import java.util.HashMap;
import java.util.Map;

import org.apache.pdfboxandroid.cos.COSArray;
import org.apache.pdfboxandroid.cos.COSBase;
import org.apache.pdfboxandroid.cos.COSDictionary;
import org.apache.pdfboxandroid.cos.COSName;
import org.apache.pdfboxandroid.cos.COSNumber;

/**
 * This is implementation for the CIDFontType0/CIDFontType2 Fonts.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.11 $
 */
public abstract class PDCIDFont extends PDSimpleFont {
	private Map<Integer,Float> widthCache = null;

	/**
     * Constructor.
     *
     * @param fontDictionary The font dictionary according to the PDF specification.
     */
    public PDCIDFont( COSDictionary fontDictionary )
    {
        super( fontDictionary );
        extractWidths();
    }
    
    private void extractWidths() 
    {
        if (widthCache == null) 
        {
            widthCache = new HashMap<Integer,Float>();
            COSArray widths = (COSArray)font.getDictionaryObject( COSName.W );
            if( widths != null )
            {
                int size = widths.size();
                int counter = 0;
                while (counter < size) 
                {
                    COSNumber firstCode = (COSNumber)widths.getObject( counter++ );
                    COSBase next = widths.getObject( counter++ );
                    if( next instanceof COSArray )
                    {
                        COSArray array = (COSArray)next;
                        int startRange = firstCode.intValue();
                        int arraySize = array.size();
                        for (int i=0; i<arraySize; i++) 
                        {
                            COSNumber width = (COSNumber)array.get(i);
                            widthCache.put(startRange+i, width.floatValue());
                        }
                    }
                    else
                    {
                        COSNumber secondCode = (COSNumber)next;
                        COSNumber rangeWidth = (COSNumber)widths.getObject( counter++ );
                        int startRange = firstCode.intValue();
                        int endRange = secondCode.intValue();
                        float width = rangeWidth.floatValue();
                        for (int i=startRange; i<=endRange; i++) {
                            widthCache.put(i,width);
                        }
                    }
                }
            }
        }
    }
}

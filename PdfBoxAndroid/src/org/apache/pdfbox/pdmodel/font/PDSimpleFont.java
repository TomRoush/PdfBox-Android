package org.apache.pdfbox.pdmodel.font;

import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class contains implementation details of the simple pdf fonts.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.18 $
 */
public abstract class PDSimpleFont extends PDFont
{

	private final HashMap<Integer, Float> mFontSizes =
	        new HashMap<Integer, Float>(128);

	    private float avgFontWidth = 0.0f;
	    private float avgFontHeight = 0.0f;
	    private float fontWidthOfSpace = -1f; 

	    private static final byte[] SPACE_BYTES = { (byte)32 };


	    /**
	     * Log instance.
	     */
	    private static final Log LOG = LogFactory.getLog(PDSimpleFont.class);
	    
	    /**
	     * Constructor.
	     */
	    public PDSimpleFont()
	    {
	        super();
	    }

	    /**
	     * This will get the font width for a character.
	     *
	     * @param c The character code to get the width for.
	     * @param offset The offset into the array.
	     * @param length The length of the data.
	     *
	     * @return The width is in 1000 unit of text space, ie 333 or 777
	     *
	     * @throws IOException If an error occurs while parsing.
	     */
	    public float getFontWidth( byte[] c, int offset, int length ) throws IOException
	    {
	        int code = getCodeFromArray( c, offset, length );
	        Float fontWidth = mFontSizes.get(code);
	        if (fontWidth == null)
	        {
	            fontWidth = getFontWidth(code);
	            if (fontWidth <= 0)
	            {
	                //hmm should this be in PDType1Font??
	                fontWidth = getFontWidthFromAFMFile( code );
	            }
	            mFontSizes.put(code, fontWidth);
	        }
	        return fontWidth;
	    }

}

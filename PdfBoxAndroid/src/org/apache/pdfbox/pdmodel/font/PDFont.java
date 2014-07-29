package org.apache.pdfbox.pdmodel.font;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.afm.AFMParser;
import org.apache.fontbox.afm.FontMetric;
import org.apache.fontbox.cmap.CMap;
import org.apache.fontbox.cmap.CMapParser;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.encoding.Encoding;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDMatrix;
import org.apache.pdfbox.util.ResourceLoader;

/**
 * This is the base class for all PDF fonts.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * 
 */
public abstract class PDFont implements COSObjectable
{
	
	/**
     * Log instance.
     */
    private static final Log LOG = LogFactory.getLog(PDFont.class);

    /**
     * The cos dictionary for this font.
     */
    protected COSDictionary font;

    /**
     * This is only used if this is a font object and it has an encoding.
     */
    private Encoding fontEncoding = null;

    /**
     *  The descriptor of the font.
     */
    private PDFontDescriptor fontDescriptor = null;

    /**
     *  The font matrix.
     */
    protected PDMatrix fontMatrix = null;

    /**
     * This is only used if this is a font object and it has an encoding and it is
     * a type0 font with a cmap.
     */
    protected CMap cmap = null;

    /**
     * The CMap holding the ToUnicode mapping.
     */
    protected CMap toUnicodeCmap = null;
    
    private boolean hasToUnicode = false;
    
    private boolean widthsAreMissing = false;

    protected static Map<String, CMap> cmapObjects =
        Collections.synchronizedMap( new HashMap<String, CMap>() );

    /**
     *  A list a floats representing the widths.
     */
    private List<Float> widths = null;
    
    /**
     * The static map of the default Adobe font metrics.
     */
    private static final Map<String, FontMetric> afmObjects =
        Collections.unmodifiableMap( getAdobeFontMetrics() );
    
 // TODO move the Map to PDType1Font as these are the 14 Standard fonts
    // which are definitely Type 1 fonts
    private static Map<String, FontMetric> getAdobeFontMetrics()
    {
        Map<String, FontMetric> metrics = new HashMap<String, FontMetric>();
        addAdobeFontMetric( metrics, "Courier-Bold" );
        addAdobeFontMetric( metrics, "Courier-BoldOblique" );
        addAdobeFontMetric( metrics, "Courier" );
        addAdobeFontMetric( metrics, "Courier-Oblique" );
        addAdobeFontMetric( metrics, "Helvetica" );
        addAdobeFontMetric( metrics, "Helvetica-Bold" );
        addAdobeFontMetric( metrics, "Helvetica-BoldOblique" );
        addAdobeFontMetric( metrics, "Helvetica-Oblique" );
        addAdobeFontMetric( metrics, "Symbol" );
        addAdobeFontMetric( metrics, "Times-Bold" );
        addAdobeFontMetric( metrics, "Times-BoldItalic" );
        addAdobeFontMetric( metrics, "Times-Italic" );
        addAdobeFontMetric( metrics, "Times-Roman" );
        addAdobeFontMetric( metrics, "ZapfDingbats" );

        // PDFBOX-239
        addAdobeFontMetric(metrics, "Arial", "Helvetica");
        addAdobeFontMetric(metrics, "Arial,Bold", "Helvetica-Bold");
        addAdobeFontMetric(metrics, "Arial,Italic", "Helvetica-Oblique");
        addAdobeFontMetric(metrics, "Arial,BoldItalic", "Helvetica-BoldOblique");
        
        return metrics;
    }
    
    protected static final String resourceRootCMAP = "org/apache/pdfbox/resources/cmap/";
    //TODO: get resources for cmap
    private static final String resourceRootAFM = "org/apache/pdfbox/resources/afm/";

    private static void addAdobeFontMetric(
            Map<String, FontMetric> metrics, String name )
    {
        addAdobeFontMetric(metrics, name, name);
    }
    
    private static void addAdobeFontMetric(Map<String, FontMetric> metrics, String name, String filePrefix)
    {
        try
        {
            String resource = resourceRootAFM + filePrefix + ".afm";
            InputStream afmStream = ResourceLoader.loadResource( resource );
            if( afmStream != null )
            {
                try
                {
                    AFMParser parser = new AFMParser( afmStream );
                    parser.parse();
                    metrics.put( name, parser.getResult() );
                }
                finally
                {
                    afmStream.close();
                }
            }
        }
        catch (Exception e)
        {
            // ignore
        }
    }

	/**
     * Constructor.
     */
    public PDFont()
    {
        font = new COSDictionary();
        font.setItem( COSName.TYPE, COSName.FONT );
    }
    
    /**
     * Constructor.
     *
     * @param fontDictionary The font dictionary according to the PDF specification.
     */
    public PDFont( COSDictionary fontDictionary )
    {
        font = fontDictionary;
        determineEncoding();
    }
    
    /**
     * This will get the font descriptor for this font.
     *
     * @return The font descriptor for this font.
     *
     */
    public PDFontDescriptor getFontDescriptor()
    {
        if(fontDescriptor == null)
        {
            COSDictionary fd = (COSDictionary)font.getDictionaryObject( COSName.FONT_DESC );
            if (fd != null)
            {
                fontDescriptor = new PDFontDescriptorDictionary( fd );
            }
            else
            {
                getAFM();
                if( afm != null )
                {
                    fontDescriptor = new PDFontDescriptorAFM( afm );
                }
            }
        }
        return fontDescriptor;
    }
    
    /**
     * Determines the encoding for the font.
     * This method as to be overwritten, as there are different
     * possibilities to define a mapping.
     */
    protected abstract void determineEncoding();

    /**
     * {@inheritDoc}
     */
    public COSBase getCOSObject()
    {
        return font;
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
    public abstract float getFontWidth( byte[] c, int offset, int length ) throws IOException;
    
    /**
     * This will get the width of this string for this font.
     *
     * @param string The string to get the width of.
     *
     * @return The width of the string in 1000 units of text space, ie 333 567...
     *
     * @throws IOException If there is an error getting the width information.
     */
    public float getStringWidth( String string ) throws IOException
    {
        byte[] data = string.getBytes("ISO-8859-1");
        float totalWidth = 0;
        for( int i=0; i<data.length; i++ )
        {
            totalWidth+=getFontWidth( data, i, 1 );
        }
        return totalWidth;
    }
    
    /**
     * This will get the average font width for all characters.
     *
     * @return The width is in 1000 unit of text space, ie 333 or 777
     *
     * @throws IOException If an error occurs while parsing.
     */
    public abstract float getAverageFontWidth() throws IOException;
    
    /**
     * Used for multibyte encodings.
     *
     * @param data The array of data.
     * @param offset The offset into the array.
     * @param length The number of bytes to use.
     *
     * @return The int value of data from the array.
     */
    public int getCodeFromArray( byte[] data, int offset, int length )
    {
        int code = 0;
        for( int i=0; i<length; i++ )
        {
            code <<= 8;
            code |= (data[offset+i]+256)%256;
        }
        return code;
    }
    
    /**
     * This will attempt to get the font width from an AFM file.
     *
     * @param code The character code we are trying to get.
     *
     * @return The font width from the AFM file.
     *
     * @throws IOException if we cannot find the width.
     */
    protected float getFontWidthFromAFMFile( int code ) throws IOException
    {
        float retval = 0;
        FontMetric metric = getAFM();
        if( metric != null )
        {
            String characterName = fontEncoding.getName( code );
            retval = metric.getCharacterWidth( characterName );
        }
        return retval;
    }
    
    /**
     * This will attempt to get the average font width from an AFM file.
     *
     * @return The average font width from the AFM file.
     *
     * @throws IOException if we cannot find the width.
     */
    protected float getAverageFontWidthFromAFMFile() throws IOException
    {
        float retval = 0;
        FontMetric metric = getAFM();
        if( metric != null )
        {
            retval = metric.getAverageCharacterWidth();
        }
        return retval;
    }
    
    /**
     * This will get an AFM object if one exists.
     *
     * @return The afm object from the name.
     *
     */
    protected FontMetric getAFM()
    {
        if (isType1Font() && afm == null)
        {
            COSBase baseFont = font.getDictionaryObject( COSName.BASE_FONT );
            String name = null;
            if( baseFont instanceof COSName )
            {
                name = ((COSName)baseFont).getName();
                if (name.indexOf("+") > -1)
                {
                    name = name.substring(name.indexOf("+")+1);
                }

            }
            else if ( baseFont instanceof COSString )
            {
                COSString string = (COSString)baseFont;
                name = string.getString();
            }
            if ( name != null )
            {
                afm = afmObjects.get( name );
            }
        }
        return afm;
    }

    private FontMetric afm = null;

    private COSBase encoding = null;
    /**
     * cache the {@link COSName#ENCODING} object from
     * the font's dictionary since it is called so often.
     * <p>
     * Use this method instead of
     * <pre>
     *   font.getDictionaryObject(COSName.ENCODING);
     * </pre>
     * @return the encoding
     */
    protected COSBase getEncoding()
    {
        if (encoding == null)
        {
            encoding = font.getDictionaryObject( COSName.ENCODING );
        }
        return encoding;
    }
    
    /**
     * Set the encoding object from the fonts dictionary.
     * @param encodingValue the given encoding.
     */
    protected void setEncoding(COSBase encodingValue)
    {
        font.setItem( COSName.ENCODING, encodingValue );
        encoding = encodingValue;
    }
    
    protected CMap parseCmap( String cmapRoot, InputStream cmapStream)
    {
        CMap targetCmap = null;
        if( cmapStream != null )
        {
            CMapParser parser = new CMapParser();
            try
            {
                targetCmap = parser.parse( cmapRoot, cmapStream );
                // limit the cache to external CMaps
                if (cmapRoot != null)
                {
                    cmapObjects.put( targetCmap.getName(), targetCmap );
                }
            }
            catch (IOException exception)
            {
                LOG.error("An error occurs while reading a CMap", exception);
            }
        }
        return targetCmap;
    }
    
    /**
     * The will set the encoding for this font.
     *
     * @param enc The font encoding.
     */
    public void setFontEncoding( Encoding enc )
    {
        fontEncoding = enc;
    }
    
    /**
     * This will get or create the encoder.
     *
     * @return The encoding to use.
     */
    public Encoding getFontEncoding()
    {
        return fontEncoding;
    }
    
 // Memorized values to avoid repeated dictionary lookups
    private String subtype = null;
    private boolean type1Font;
    private boolean type3Font;
    private boolean trueTypeFont;
    private boolean type0Font;

    /**
     * This will get the subtype of font, Type1, Type3, ...
     *
     * @return The type of font that this is.
     */
    public String getSubType()
    {
        if (subtype == null)
        {
            subtype = font.getNameAsString( COSName.SUBTYPE );
            type1Font = "Type1".equals(subtype);
            trueTypeFont = "TrueType".equals(subtype);
            type0Font = "Type0".equals(subtype);
            type3Font = "Type3".equals(subtype);
        }
        return subtype;
    }

    /**
     * Determines if the font is a type 1 font.
     * @return returns true if the font is a type 1 font
     */
    protected boolean isType1Font()
    {
        getSubType();
        return type1Font;
    }
    
    /**
     * The PostScript name of the font.
     *
     * @return The postscript name of the font.
     */
    public String getBaseFont()
    {
        return font.getNameAsString( COSName.BASE_FONT );
    }
    
    /**
     * Set the PostScript name of the font.
     *
     * @param baseFont The postscript name for the font.
     */
    public void setBaseFont( String baseFont )
    {
        font.setName( COSName.BASE_FONT, baseFont );
    }
    
    /**
     * The code for the first char or -1 if there is none.
     *
     * @return The code for the first character.
     */
    public int getFirstChar()
    {
        return font.getInt( COSName.FIRST_CHAR, -1 );
    }

    /**
     * Set the first character this font supports.
     *
     * @param firstChar The first character.
     */
    public void setFirstChar( int firstChar )
    {
        font.setInt( COSName.FIRST_CHAR, firstChar );
    }

    /**
     * The code for the last char or -1 if there is none.
     *
     * @return The code for the last character.
     */
    public int getLastChar()
    {
        return font.getInt( COSName.LAST_CHAR, -1 );
    }

    /**
     * Set the last character this font supports.
     *
     * @param lastChar The last character.
     */
    public void setLastChar( int lastChar )
    {
        font.setInt( COSName.LAST_CHAR, lastChar );
    }
    
    /**
     * The widths of the characters.  This will be null for the standard 14 fonts.
     *
     * @return The widths of the characters.
     *
     */
    public List<Float> getWidths()
    {
        if (widths == null && !widthsAreMissing)
        {
            COSArray array = (COSArray)font.getDictionaryObject( COSName.WIDTHS );
            if (array != null)
            {
                widths = COSArrayList.convertFloatCOSArrayToList(array);
            }
            else
            {
                widthsAreMissing = true;
            }
        }
        return widths;
    }

    /**
     * Set the widths of the characters code.
     *
     * @param widthsList The widths of the character codes.
     */
    public void setWidths(List<Float> widthsList)
    {
        widths = widthsList;
        font.setItem( COSName.WIDTHS, COSArrayList.converterToCOSArray( widths ) );
    }
    
    /**
     * Determines the width of the given character.
     * @param charCode the code of the given character
     * @return the width of the character
     */
    public float getFontWidth( int charCode )
    {
        float width = -1;
        int firstChar = getFirstChar();
        int lastChar = getLastChar();
        if (charCode >= firstChar && charCode <= lastChar)
        {
            // maybe the font doesn't provide any widths
            if (!widthsAreMissing)
            {
                getWidths();
                if (widths != null)
                {
                    width = widths.get(charCode-firstChar).floatValue();
                }
            }
        }
        else
        {
            PDFontDescriptor fd = getFontDescriptor();
            if (fd instanceof PDFontDescriptorDictionary)
            {
                width = fd.getMissingWidth();
            }
        }
        return width;
    }
    
    /**
     * Sets hasToUnicode to the given value.
     * @param hasToUnicodeValue the given value for hasToUnicode
     */
    protected void setHasToUnicode(boolean hasToUnicodeValue)
    {
        hasToUnicode = hasToUnicodeValue;
    }

}

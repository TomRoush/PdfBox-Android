package org.apache.pdfboxandroid.pdmodel.font;

import org.apache.pdfboxandroid.cos.COSArray;
import org.apache.pdfboxandroid.cos.COSBase;
import org.apache.pdfboxandroid.cos.COSDictionary;
import org.apache.pdfboxandroid.cos.COSName;
import org.apache.pdfboxandroid.cos.COSStream;
import org.apache.pdfboxandroid.cos.COSString;
import org.apache.pdfboxandroid.pdmodel.common.COSObjectable;
import org.apache.pdfboxandroid.pdmodel.common.PDRectangle;
import org.apache.pdfboxandroid.pdmodel.common.PDStream;

/**
 * This class represents an implementation to the font descriptor that gets its
 * information from a COS Dictionary.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.4 $
 */
public class PDFontDescriptorDictionary extends PDFontDescriptor implements COSObjectable {
	private COSDictionary dic;
    private float xHeight = Float.NEGATIVE_INFINITY;
    private float capHeight = Float.NEGATIVE_INFINITY;
    private int flags = -1;
    
    /**
     * Constructor.
     */
    public PDFontDescriptorDictionary()
    {
        dic = new COSDictionary();
        dic.setItem( COSName.TYPE, COSName.FONT_DESC );
    }
    
    /**
     * Constructor.
     *
     * @param desc The wrapped COS Dictionary.
     */
    public PDFontDescriptorDictionary( COSDictionary desc )
    {
        dic = desc;
    }
    
    /**
     * A stream containing a font program that is not true type or type 1.
     *
     * @return A stream containing a font program.
     */
    public PDStream getFontFile3()
    {
        PDStream retval = null;
        COSStream stream = (COSStream)dic.getDictionaryObject( COSName.FONT_FILE3 );
        if( stream != null )
        {
            retval = new PDStream( stream );
        }
        return retval;
    }

	/**
     * Convert this standard java object to a COS object.
     *
     * @return The cos object that matches this Java object.
     */
    public COSBase getCOSObject()
    {
        return dic;
    }
    
    /**
     * Get the font name.
     *
     * @return The name of the font.
     */
    public String getFontName()
    {
        String retval = null;
        COSName name = (COSName)dic.getDictionaryObject( COSName.FONT_NAME );
        if( name != null )
        {
            retval = name.getName();
        }
        return retval;
    }
    
    /**
     * This will get the dictionary for this object.
     *
     * @return The COS dictionary.
     */
    public COSDictionary getCOSDictionary()
    {
        return dic;
    }
    
    /**
     * This will set the font name.
     *
     * @param fontName The new name for the font.
     */
    public void setFontName( String fontName )
    {
        COSName name = null;
        if( fontName != null )
        {
            name = COSName.getPDFName( fontName );
        }
        dic.setItem( COSName.FONT_NAME, name );
    }
    
    /**
     * This will set the font family.
     *
     * @param fontFamily The font family.
     */
    public void setFontFamily( String fontFamily )
    {
        COSString name = null;
        if( fontFamily != null )
        {
            name = new COSString( fontFamily );
        }
        dic.setItem( COSName.FONT_FAMILY, name );
    }

    /**
     * This will get the font flags.
     *
     * @return The font flags.
     */
    public int getFlags()
    {
        if (flags == -1)
        {
            flags = dic.getInt( COSName.FLAGS, 0 );
        }
        return flags;
    }

    /**
     * This will set the font flags.
     *
     * @param flags The new font flags.
     */
    public void setFlags( int flags )
    {
        dic.setInt( COSName.FLAGS, flags );
        this.flags = flags;
    }
    
    /**
     * This will set the font stretch.
     *
     * @param fontStretch The new stretch for the font.
     */
    public void setFontStretch( String fontStretch )
    {
        COSName name = null;
        if( fontStretch != null )
        {
            name = COSName.getPDFName( fontStretch );
        }
        dic.setItem( COSName.FONT_STRETCH, name );
    }
    
    /**
     * Set the weight of the font.
     *
     * @param fontWeight The new weight of the font.
     */
    public void setFontWeight( float fontWeight )
    {
        dic.setFloat( COSName.FONT_WEIGHT, fontWeight );
    }
    
    /**
     * Set the fonts bounding box.
     *
     * @param rect The new bouding box.
     */
    public void setFontBoundingBox( PDRectangle rect )
    {
        COSArray array = null;
        if( rect != null )
        {
            array = rect.getCOSArray();
        }
        dic.setItem( COSName.FONT_BBOX, array );
    }
    
    /**
     * This will set the ascent for the font.
     *
     * @param ascent The new ascent for the font.
     */
    public void setAscent( float ascent )
    {
        dic.setFloat( COSName.ASCENT, ascent );
    }

    /**
     * This will set the descent for the font.
     *
     * @param descent The new descent for the font.
     */
    public void setDescent( float descent )
    {
        dic.setFloat( COSName.DESCENT, descent );
    }
    
    /**
     * This will set the italic angle for the font.
     *
     * @param angle The new italic angle for the font.
     */
    public void setItalicAngle( float angle )
    {
        dic.setFloat( COSName.ITALIC_ANGLE, angle );
    }
    
    /**
     * This will set the cap height for the font.
     *
     * @param capHeight The new cap height for the font.
     */
    public void setCapHeight( float capHeight )
    {
        dic.setFloat( COSName.CAP_HEIGHT, capHeight );
        this.capHeight = capHeight;
    }
    
    /**
     * This will set the x height for the font.
     *
     * @param xHeight The new x height for the font.
     */
    public void setXHeight( float xHeight )
    {
        dic.setFloat( COSName.XHEIGHT, xHeight );
        this.xHeight = xHeight;
    }
    
    /**
     * This will get the fonts bounding box.
     *
     * @return The fonts bounding box.
     */
    public PDRectangle getFontBoundingBox()
    {
        COSArray rect = (COSArray)dic.getDictionaryObject( COSName.FONT_BBOX );
        PDRectangle retval = null;
        if( rect != null )
        {
            retval = new PDRectangle( rect );
        }
        return retval;
    }
    
    /**
     * This will set the stem V for the font.
     *
     * @param stemV The new stem v for the font.
     */
    public void setStemV( float stemV )
    {
        dic.setFloat( COSName.STEM_V, stemV );
    }

    /**
     * This will get the missing width for the font.
     *
     * @return The missing width value.
     */
    public float getMissingWidth()
    {
        return dic.getFloat( COSName.MISSING_WIDTH, 0 );
    }

    /**
     * This will get the descent for the font.
     *
     * @return The descent.
     */
    public float getDescent()
    {
        return dic.getFloat( COSName.DESCENT, 0 );
    }
}

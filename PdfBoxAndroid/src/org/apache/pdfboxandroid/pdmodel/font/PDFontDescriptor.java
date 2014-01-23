package org.apache.pdfboxandroid.pdmodel.font;

import org.apache.pdfboxandroid.pdmodel.common.PDRectangle;

/**
 * This class represents an interface to the font description.  This will depend
 * on the font type for the actual implementation.  If it is a AFM/cmap/or embedded font.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.2 $
 */
public abstract class PDFontDescriptor {
	/**
     * A font descriptor flag.  See PDF Reference for description.
     */
    private static final int FLAG_FIXED_PITCH = 1;
	/**
     * A font descriptor flag.  See PDF Reference for description.
     */
    private static final int FLAG_SERIF = 2;
    /**
     * A font descriptor flag.  See PDF Reference for description.
     */
    private static final int FLAG_SYMBOLIC = 4;
	/**
     * A font descriptor flag.  See PDF Reference for description.
     */
    private static final int FLAG_SCRIPT = 8;
    /**
     * A font descriptor flag.  See PDF Reference for description.
     */
    private static final int FLAG_NON_SYMBOLIC = 32;
	
	/**
     * Get the font name.
     *
     * @return The name of the font.
     */
    public abstract String getFontName();
    
    /**
     * A convenience method that sets the flag bit.
     *
     * @param flag The flag value.
     */
    public void setScript( boolean flag )
    {
        setFlagBit( FLAG_SCRIPT, flag );
    }
    
    private void setFlagBit( int bit, boolean value )
    {
        int flags = getFlags();
        if( value )
        {
            flags = flags | bit;
        }
        else
        {
            flags = flags & (0xFFFFFFFF ^ bit);
        }
        setFlags( flags );
    }
    
    /**
     * This will get the font flags.
     *
     * @return The font flags.
     */
    public abstract int getFlags();

    /**
     * This will set the font flags.
     *
     * @param flags The new font flags.
     */
    public abstract void setFlags( int flags );
    
    /**
     * A convenience method that sets the flag bit.
     *
     * @param flag The flag value.
     */
    public void setSerif( boolean flag )
    {
        setFlagBit( FLAG_SERIF, flag );
    }
    
    /**
     * A convenience method that sets the flag bit.
     *
     * @param flag The flag value.
     */
    public void setSymbolic( boolean flag )
    {
        setFlagBit( FLAG_SYMBOLIC, flag );
    }
    
    /**
     * A convenience method that sets the flag bit.
     *
     * @param flag The flag value.
     */
    public void setNonSymbolic( boolean flag )
    {
        setFlagBit( FLAG_NON_SYMBOLIC, flag );
    }
    
    /**
     * A convenience method that sets the flag bit.
     *
     * @param flag The flag value.
     */
    public void setFixedPitch( boolean flag )
    {
        setFlagBit( FLAG_FIXED_PITCH, flag );
    }
    
    /**
     * A convenience method that checks the flag bit.
     *
     * @return The flag value.
     */
    public boolean isFixedPitch()
    {
        return isFlagBitOn( FLAG_FIXED_PITCH );
    }
    
    private boolean isFlagBitOn( int bit )
    {
        return (getFlags() & bit) != 0;
    }
    
    /**
     * This will get the missing width for the font.
     *
     * @return The missing width value.
     */
    public abstract float getMissingWidth();
    
    /**
     * This will get the fonts bouding box.
     *
     * @return The fonts bouding box.
     */
    public abstract PDRectangle getFontBoundingBox();
    
    /**
     * This will get the descent for the font.
     *
     * @return The descent.
     */
    public abstract float getDescent();
}

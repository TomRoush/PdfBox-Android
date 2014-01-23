package org.apache.pdfboxandroid.pdmodel.font;

import org.apache.fontbox.afm.FontMetric;
import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfboxandroid.pdmodel.common.PDRectangle;

/**
 * This class represents the font descriptor when the font information
 * is coming from an AFM file.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.3 $
 */
public class PDFontDescriptorAFM extends PDFontDescriptor {
	private FontMetric afm;

	/**
	 * Constructor.
	 *
	 * @param afmFile The AFM file.
	 */
	public PDFontDescriptorAFM( FontMetric afmFile )
	{
		afm = afmFile;
	}

	/**
	 * Get the font name.
	 *
	 * @return The name of the font.
	 */
	public String getFontName()
	{
		return afm.getFontName();
	}

	/**
	 * This will get the font flags.
	 *
	 * @return The font flags.
	 */
	public int getFlags()
	{
		//I believe that the only flag that AFM supports is the is fixed pitch
		return afm.isFixedPitch() ? 1 : 0;
	}

	/**
	 * This will set the font flags.
	 *
	 * @param flags The new font flags.
	 */
	public void setFlags( int flags )
	{
		throw new UnsupportedOperationException( "The AFM Font descriptor is immutable" );
	}

	/**
	 * This will get the missing width for the font.
	 *
	 * @return The missing width value.
	 */
	public float getMissingWidth()
	{
		return 0;
	}

	/**
     * This will get the fonts bouding box.
     *
     * @return The fonts bouding box.
     */
    public PDRectangle getFontBoundingBox()
    {
        BoundingBox box = afm.getFontBBox();
        PDRectangle retval = null;
        if( box != null )
        {
            retval = new PDRectangle( box );
        }
        return retval;
    }

	/**
     * This will get the descent for the font.
    *
    * @return The descent.
    */
   public float getDescent()
   {
       return afm.getDescender();
   }
}

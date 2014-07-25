package org.apache.pdfbox.util;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;

/**
 * This contains all of the image parameters for in inlined image.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.4 $
 */
public class ImageParameters
{

	private COSDictionary dictionary;

    /**
     * Constructor.
     */
    public ImageParameters()
    {
        dictionary = new COSDictionary();
    }

    /**
     * Constructor.
     *
     * @param params The image parameters.
     */
    public ImageParameters( COSDictionary params )
    {
        dictionary = params;
    }

    /**
     * This will get the dictionary that stores the image parameters.
     *
     * @return The COS dictionary that stores the image parameters.
     */
    public COSDictionary getDictionary()
    {
        return dictionary;
    }

    private COSBase getCOSObject( COSName abbreviatedName, COSName name )
    {
        COSBase retval = dictionary.getDictionaryObject( abbreviatedName );
        if( retval == null )
        {
            retval = dictionary.getDictionaryObject( name );
        }
        return retval;
    }

    private int getNumberOrNegativeOne( COSName abbreviatedName, COSName name )
    {
        int retval = -1;
        COSNumber number = (COSNumber)getCOSObject( abbreviatedName, name );
        if( number != null )
        {
            retval = number.intValue();
        }
        return retval;
    }

    /**
     * The bits per component of this image.  This will return -1 if one has not
     * been set.
     *
     * @return The number of bits per component.
     */
    public int getBitsPerComponent()
    {
        return getNumberOrNegativeOne( COSName.BPC, COSName.BITS_PER_COMPONENT );
    }

    /**
     * Set the number of bits per component.
     *
     * @param bpc The number of bits per component.
     */
    public void setBitsPerComponent( int bpc )
    {
        dictionary.setInt( COSName.BPC, bpc );
    }

}

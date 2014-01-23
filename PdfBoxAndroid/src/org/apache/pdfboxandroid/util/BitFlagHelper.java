package org.apache.pdfboxandroid.util;

import org.apache.pdfboxandroid.cos.COSDictionary;
import org.apache.pdfboxandroid.cos.COSName;

/**
 * This class will be used for bit flag operations.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.4 $
 */
public class BitFlagHelper {
	/**
     * Gets the boolean value from the flags at the given bit
     * position.
     *
     * @param dic The dictionary to get the field from.
     * @param field The COSName of the field to get the flag from.
     * @param bitFlag the bitPosition to get the value from.
     *
     * @return true if the number at bitPos is '1'
     */
    public static final boolean getFlag(COSDictionary dic, COSName field, int bitFlag)
    {
        int ff = dic.getInt( field, 0 );
        return (ff & bitFlag) == bitFlag;
    }
}

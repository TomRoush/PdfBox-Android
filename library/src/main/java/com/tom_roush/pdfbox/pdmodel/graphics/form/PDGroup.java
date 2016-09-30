package org.apache.pdfbox.pdmodel.graphics.form;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;

/**
 * Transparency group.
 * 
 * @author K�hn & Weyh Software, GmbH
 */
public final class PDGroup implements COSObjectable
{
    private COSDictionary dictionary;
    private COSName subType;
    private PDColorSpace colorSpace;

    /**
     * Creates a group object from a given dictionary
     * @param dic {@link COSDictionary} object
     */
    public PDGroup(COSDictionary dic)
    {
        dictionary = dic;
    }

    public COSBase getCOSObject()
    {
        return dictionary;
    }

    public COSDictionary getCOSDictionary()
    {
        return dictionary;
    }

    /**
     * Returns the groups's subtype, should be "Transparency".
     */
    public COSName getSubType()
    {
        if (subType == null)
        {
            subType = (COSName) getCOSDictionary().getDictionaryObject(COSName.S);
        }
        return subType;
    }

    /**
     * Returns the blending color space
     * @return color space
     * @throws IOException
     */
//    public PDColorSpace getColorSpace() throws IOException
//    {
//        if (colorSpace == null)
//        {
//            colorSpace = PDColorSpace.create(getCOSDictionary().getDictionaryObject(
//                    COSName.COLORSPACE));
//        }
//        return colorSpace;
//    }

    /**
     * Returns true if this group is isolated. Isolated groups begin with the fully transparent
     * image, non-isolated begin with the current backdrop.
     */
    public boolean isIsolated()
    {
        return getCOSDictionary().getBoolean(COSName.I, false);
    }

    /**
     * Returns true if this group is a knockout. A knockout group blends with original backdrop,
     * a non-knockout group blends with the current backdrop.
     */
    public boolean isKnockout()
    {
        return getCOSDictionary().getBoolean(COSName.K, false);
    }
}

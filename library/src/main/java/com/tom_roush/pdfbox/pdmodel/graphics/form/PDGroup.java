package com.tom_roush.pdfbox.pdmodel.graphics.form;

import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.pdmodel.common.COSObjectable;
import com.tom_roush.pdfbox.pdmodel.graphics.color.PDColorSpace;

import java.io.IOException;

/**
 * Transparency group.
 * 
 * @author K�hn & Weyh Software, GmbH
 */
public final class PDGroup implements COSObjectable
{
    private final COSDictionary dictionary;
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

    @Override
    public COSDictionary getCOSObject()
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
            subType = (COSName) getCOSObject().getDictionaryObject(COSName.S);
        }
        return subType;
    }

    /**
     * Returns the blending color space
     *
     * @return color space
     * @throws IOException
     */
    public PDColorSpace getColorSpace() throws IOException
    {
        if (colorSpace == null)
        {
            colorSpace = PDColorSpace.create(getCOSObject().getDictionaryObject(COSName.CS));
        }
        return colorSpace;
    }

    /**
     * Returns true if this group is isolated. Isolated groups begin with the fully transparent
     * image, non-isolated begin with the current backdrop.
     */
    public boolean isIsolated()
    {
        return getCOSObject().getBoolean(COSName.I, false);
    }

    /**
     * Returns true if this group is a knockout. A knockout group blends with original backdrop,
     * a non-knockout group blends with the current backdrop.
     */
    public boolean isKnockout()
    {
        return getCOSObject().getBoolean(COSName.K, false);
    }
}

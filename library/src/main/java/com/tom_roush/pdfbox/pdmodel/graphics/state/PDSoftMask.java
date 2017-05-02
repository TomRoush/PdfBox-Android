package com.tom_roush.pdfbox.pdmodel.graphics.state;

import android.util.Log;

import com.tom_roush.pdfbox.cos.COSArray;
import com.tom_roush.pdfbox.cos.COSBase;
import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.pdmodel.common.COSObjectable;
import com.tom_roush.pdfbox.pdmodel.common.function.PDFunction;
import com.tom_roush.pdfbox.pdmodel.graphics.PDXObject;
import com.tom_roush.pdfbox.pdmodel.graphics.form.PDFormXObject;

import java.io.IOException;

/**
 * Soft mask.
 *
 * @author K�hn & Weyh Software, GmbH
 */
public final class PDSoftMask implements COSObjectable
{
    /**
     * Creates a new soft mask.
     *
     * @param dictionary SMask
     */
    public static PDSoftMask create(COSBase dictionary)
    {
        if (dictionary instanceof COSName)
        {
            if (COSName.NONE.equals(dictionary))
            {
                return null;
            }
            else
            {
            	Log.w("PdfBox-Android", "Invalid SMask " + dictionary);
                return null;
            }
        }
        else if (dictionary instanceof COSDictionary)
        {
            return new PDSoftMask((COSDictionary) dictionary);
        }
        else
        {
        	Log.w("PdfBox-Android", "Invalid SMask " + dictionary);
            return null;
        }
    }

    private COSDictionary dictionary;
    private COSName subType = null;
    private PDFormXObject group = null;
    private COSArray backdropColor = null;
    private PDFunction transferFunction = null;

    /**
     * Creates a new soft mask.
     */
    public PDSoftMask(COSDictionary dictionary)
    {
        super();
        this.dictionary = dictionary;
    }

    @Override
    public COSDictionary getCOSObject()
    {
        return dictionary;
    }

    /**
     * Returns the subtype of the soft mask (Alpha, Luminosity) - S entry
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
     * Returns the G entry of the soft mask object
     * 
     * @return form containing the transparency group
     * @throws IOException
     */
    public PDFormXObject getGroup() throws IOException
    {
        if (group == null)
        {
            COSBase cosGroup = getCOSObject().getDictionaryObject(COSName.G);
            if (cosGroup != null)
            {
                group = (PDFormXObject) PDXObject.createXObject(cosGroup, null);
            }
        }
        return group;
    }

    /**
     * Returns the backdrop color.
     */
    public COSArray getBackdropColor()
    {
        if (backdropColor == null)
        {
            backdropColor = (COSArray) getCOSObject().getDictionaryObject(COSName.BC);
        }
        return backdropColor;
    }

    /**
     * Returns the transfer function.
     */
    public PDFunction getTransferFunction() throws IOException
    {
        if (transferFunction == null)
        {
            COSBase cosTF = getCOSObject().getDictionaryObject(COSName.TR);
            if (cosTF != null)
            {
                transferFunction = PDFunction.create(cosTF);
            }
        }
        return transferFunction;
    }
}

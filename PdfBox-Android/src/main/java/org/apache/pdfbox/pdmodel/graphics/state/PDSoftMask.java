package org.apache.pdfbox.pdmodel.graphics.state;

import java.io.IOException;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.function.PDFunction;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;

import android.util.Log;

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
            	Log.w("PdfBoxAndroid", "Invalid SMask " + dictionary);
                return null;
            }
        }
        else if (dictionary instanceof COSDictionary)
        {
            return new PDSoftMask((COSDictionary) dictionary);
        }
        else
        {
        	Log.w("PdfBoxAndroid", "Invalid SMask " + dictionary);
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

    public COSBase getCOSObject()
    {
        return dictionary;
    }

    public COSDictionary getCOSDictionary()
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
            subType = (COSName) getCOSDictionary().getDictionaryObject(COSName.S);
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
            COSBase cosGroup = getCOSDictionary().getDictionaryObject(COSName.G);
            if (cosGroup != null)
            {
                group = (PDFormXObject) PDXObject
                        .createXObject(cosGroup, COSName.G.getName(), null);
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
            backdropColor = (COSArray) getCOSDictionary().getDictionaryObject(COSName.BC);
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
            COSBase cosTF = getCOSDictionary().getDictionaryObject(COSName.TR);
            if (cosTF != null)
            {
                transferFunction = PDFunction.create(cosTF);
            }
        }
        return transferFunction;
    }
}

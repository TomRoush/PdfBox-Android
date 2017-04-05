package com.tom_roush.pdfbox.pdmodel.interactive.form;

import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSName;

/**
 * A scrollable list box. Contains several text items, one or more of which shall be selected as the field value.
 *
 * @author John Hewson
 */
public final class PDListBox extends PDChoice
{
    /**
     * @param theAcroForm The acroform.
     * @see PDFieldTreeNode#PDFieldTreeNode(PDAcroForm)
     */
    public PDListBox(PDAcroForm theAcroForm)
    {
        super(theAcroForm);
    }

    /**
     * Constructor.
     *
     * @param acroForm The form that this field is part of.
     * @param field the PDF object to represent as a field.
     * @param parentNode the parent node of the node to be created
     */
    public PDListBox(PDAcroForm acroForm, COSDictionary field, PDFieldTreeNode parentNode)
    {
        super(acroForm, field, parentNode);
    }

    /**
     * This will get the top index "TI" value.
     *
     * @return the top index, default value 0.
     */
    public int getTopIndex()
    {
        return getCOSObject().getInt(COSName.TI, 0);
    }

    /**
     * This will set top index "TI" value.
     *
     * @param topIndex the value for the top index, null will remove the value.
     */
    public void setTopIndex(Integer topIndex)
    {
        if (topIndex != null)
        {
            getCOSObject().setInt(COSName.TI, topIndex);
        }
        else
        {
            getCOSObject().removeItem(COSName.TI);
        }
    }
}
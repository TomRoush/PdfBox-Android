package com.tom_roush.pdfbox.pdmodel.interactive.form;

import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSName;

import java.io.IOException;

/**
 * A scrollable list box. Contains several text items, one or more of which shall be selected as the
 * field value.
 *
 * @author John Hewson
 */
public final class PDListBox extends PDChoice
{
    /**
     * @see PDField#PDField(PDAcroForm)
     *
     * @param acroForm The acroform.
     */
    public PDListBox(PDAcroForm acroForm)
    {
        super(acroForm);
    }

    /**
     * Constructor.
     *
     * @param acroForm The form that this field is part of.
     * @param field the PDF object to represent as a field.
     * @param parent the parent node of the node
     */
    PDListBox(PDAcroForm acroForm, COSDictionary field, PDNonTerminalField parent)
    {
        super(acroForm, field, parent);
    }

    /**
     * This will get the top index "TI" value.
     *
     * @return the top index, default value 0.
     */
    public int getTopIndex()
    {
        return dictionary.getInt(COSName.TI, 0);
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
            dictionary.setInt(COSName.TI, topIndex);
        }
        else
        {
            dictionary.removeItem(COSName.TI);
        }
    }

    @Override
    void constructAppearances() throws IOException
    {
        AppearanceGeneratorHelper apHelper;
        apHelper = new AppearanceGeneratorHelper(this);
        apHelper.setAppearanceValue("");
    }
}
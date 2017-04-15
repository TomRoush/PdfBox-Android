package com.tom_roush.pdfbox.pdmodel.interactive.form;

import com.tom_roush.pdfbox.cos.COSBase;
import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSName;

import java.io.IOException;

/**
 * A check box toggles between two states, on and off.
 *
 * @author Ben Litchfield
 * @author sug
 */
public final class PDCheckbox extends PDButton
{
    /**
     * @see PDField#PDField(PDAcroForm)
     *
     * @param acroForm The acroform.
     */
    public PDCheckbox(PDAcroForm acroForm)
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
    public PDCheckbox(PDAcroForm acroForm, COSDictionary field, PDNonTerminalField parent)
    {
        super(acroForm, field, parent);
    }

    /**
     * This will tell if this radio button is currently checked or not.
     * This is equivalent to calling {@link #getValue()}.
     *
     * @return true If this field is checked.
     */
    public boolean isChecked()
    {
        return getValue();
    }

    /**
     * Checks the check box.
     */
    public void check() throws IOException
    {
        setValue(true);
    }

    /**
     * Unchecks the check box.
     */
    public void unCheck() throws IOException
    {
        setValue(false);
    }

    /**
     * Returns true if this field is checked.
     *
     * @return True if checked
     */
    public boolean getValue()
    {
        COSBase value = getInheritableAttribute(COSName.V);
        return value instanceof COSName && value.equals(COSName.YES);
    }

    /**
     * Returns the default value, if any.
     *
     * @return True if checked, false if not checked, null if missing.
     */
    public Boolean getDefaultValue()
    {
        COSBase value = getInheritableAttribute(COSName.DV);
        if (value == null)
        {
            return null;
        }
        return value instanceof COSName && value.equals(COSName.YES);
    }

    @Override
    public String getValueAsString()
    {
        return getValue() ? "Yes" : "Off";
    }

    /**
     * Sets the checked value of this field.
     *
     * @param value True if checked
     * @throws IOException if the value could not be set
     */
    public void setValue(boolean value) throws IOException
    {
        COSName name = value ? COSName.YES : COSName.OFF;
        dictionary.setItem(COSName.V, name);

        // update the appearance state (AS)
        dictionary.setItem(COSName.AS, name);

        applyChange();
    }

    /**
     * Sets the default value.
     *
     * @param value True if checked
     * @throws IOException if the value could not be set
     */
    public void setDefaultValue(boolean value) throws IOException
    {
        COSName name = value ? COSName.YES : COSName.OFF;
        dictionary.setItem(COSName.DV, name);
    }
}

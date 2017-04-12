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
     *
     * @return true If the radio button is checked.
     * @throws IOException
     */
    public boolean isChecked() throws IOException
    {
        String onValue = getOnValue();
        String fieldValue = null;
        try
        {
            fieldValue = getValue();
        }
        catch (IOException e)
        {
            // getting there means that the field value
            // doesn't have a supported type.
            // Ignoring as that will also mean that the field is not checked.
            // Setting the value explicitly as Code Analysis (Sonar) doesn't like
            // empty catch blocks.
            return false;
        }
        COSName radioValue = (COSName) dictionary.getDictionaryObject(COSName.AS);
        return radioValue != null && fieldValue != null && radioValue.getName().equals(onValue);

    }

    /**
     * Checks the check box.
     */
    public void check()
    {
        String onValue = getOnValue();
        setValue(onValue);
        dictionary.setItem(COSName.AS, COSName.getPDFName(onValue));
    }

    /**
     * Unchecks the check box.
     */
    public void unCheck()
    {
        dictionary.setItem(COSName.AS, COSName.OFF);
    }

    /**
     * This will get the value assigned to the OFF state.
     *
     * @return The value of the check box.
     */
    public String getOffValue()
    {
        return COSName.OFF.getName();
    }

    /**
     * This will get the value assigned to the ON state.
     *
     * @return The value of the check box.
     */
    public String getOnValue()
    {
        COSDictionary ap = (COSDictionary) dictionary.getDictionaryObject(COSName.AP);
        COSBase n = ap.getDictionaryObject(COSName.N);

        //N can be a COSDictionary or a COSStream
        if (n instanceof COSDictionary)
        {
            for (COSName key : ((COSDictionary) n).keySet())
            {
                if (!key.equals(COSName.OFF))
                {
                    return key.getName();
                }
            }
        }
        return "";
    }

    @Override
    public String getValue() throws IOException
    {
        COSBase attribute = getInheritableAttribute(COSName.V);

        if (attribute == null)
        {
            return "";
        }
        else if (attribute instanceof COSName)
        {
            return ((COSName) attribute).getName();
        }
        else
        {
            throw new IOException("Expected a COSName entry but got " + attribute.getClass().getName());
        }
    }

    @Override
    public void setValue(String value)
    {
        if (value == null)
        {
            dictionary.removeItem(COSName.V);
            dictionary.setItem(COSName.AS, COSName.OFF);
        }
        else
        {
            COSName nameValue = COSName.getPDFName(value);
            dictionary.setItem(COSName.V, nameValue);
            dictionary.setItem(COSName.AS, nameValue);
        }
    }
}

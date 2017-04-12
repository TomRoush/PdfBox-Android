package com.tom_roush.pdfbox.pdmodel.interactive.form;

import com.tom_roush.pdfbox.cos.COSArray;
import com.tom_roush.pdfbox.cos.COSBase;
import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.cos.COSString;
import com.tom_roush.pdfbox.pdmodel.common.COSArrayList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A button field represents an interactive control on the screen
 * that the user can manipulate with the mouse.
 *
 * @author sug
 */
public abstract class PDButton extends PDTerminalField
{
    /**
     * A Ff flag. If set, the field is a set of radio buttons
     */
    static final int FLAG_RADIO = 1 << 15;
    /**
     * A Ff flag. If set, the field is a pushbutton.
     */
    static final int FLAG_PUSHBUTTON = 1 << 16;
    /**
     * A Ff flag. If set, radio buttons individual fields, using the same
     * value for the on state will turn on and off in unison.
     */
    static final int FLAG_RADIOS_IN_UNISON = 1 << 25;

    /**
     * @see PDField#PDField(PDAcroForm) (PDAcroForm)
     *
     * @param acroForm The acroform.
     */
    public PDButton(PDAcroForm acroForm)
    {
        super(acroForm);
        dictionary.setItem(COSName.FT, COSName.BTN);
    }

    /**
     * Constructor.
     *
     * @param acroForm The form that this field is part of.
     * @param field the PDF object to represent as a field.
     * @param parent the parent node of the node
     */
    PDButton(PDAcroForm acroForm, COSDictionary field, PDNonTerminalField parent)
    {
        super(acroForm, field, parent);
    }

    /**
     * Determines if push button bit is set.
     *
     * @return true if type of button field is a push button.
     */
    public boolean isPushButton()
    {
        return dictionary.getFlag(COSName.FF, FLAG_PUSHBUTTON);
    }

    /**
     * Set the push button bit.
     *
     * @param pushbutton if true the button field is treated as a push button field.
     */
    public void setPushButton(boolean pushbutton)
    {
        dictionary.setFlag(COSName.FF, FLAG_PUSHBUTTON, pushbutton);
    }

    /**
     * Determines if radio button bit is set.
     *
     * @return true if type of button field is a push button.
     */
    public boolean isRadioButton()
    {
        return dictionary.getFlag(COSName.FF, FLAG_RADIO);
    }

    /**
     * Set the radio button bit.
     *
     * @param radiobutton if true the button field is treated as a radio button field.
     */
    public void setRadioButton(boolean radiobutton)
    {
        dictionary.setFlag(COSName.FF, FLAG_RADIO, radiobutton);
    }

    @Override
    public String getDefaultValue() throws IOException
    {
        COSBase attribute = getInheritableAttribute(COSName.DV);

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
    public void setDefaultValue(String defaultValue)
    {
        if (defaultValue == null)
        {
            dictionary.removeItem(COSName.DV);
        }
        else
        {
            dictionary.setItem(COSName.DV, COSName.getPDFName(defaultValue));
        }
    }

    /**
     * This will get the option values - the "Opt" entry.
     * <p>
     * <p>The option values are used to define the export values
     * for the field to
     * <ul>
     * <li>hold values in non-Latin writing systems as name objects, which represent the field value, are limited
     * to PDFDocEncoding
     * </li>
     * <li>allow radio buttons having the same export value to be handled independently
     * </li>
     * </ul>
     * </p>
     *
     * @return A list of java.lang.String values.
     */
    public List<String> getOptions()
    {
        COSBase value = getInheritableAttribute(COSName.OPT);
        if (value instanceof COSString)
        {
            List<String> array = new ArrayList<String>();
            array.add(((COSString) value).getString());
            return array;
        }
        else if (value instanceof COSArray)
        {
            return COSArrayList.convertCOSStringCOSArrayToList((COSArray) value);
        }
        return Collections.emptyList();
    }

    /**
     * This will set the options.
     *
     * @param values List containing all possible options. Supplying null list will remove the Opt entry.
     * @see #getOptions()
     */
    public void setOptions(List<String> values)
    {
        COSArray cosValues = null;
        if (values != null)
        {
            cosValues = COSArrayList.convertStringListToCOSStringCOSArray(values);
        }
        dictionary.setItem(COSName.OPT, cosValues);
    }

    @Override
    void constructAppearances() throws IOException
    {
        // TODO: implement appearance generation for buttons
        throw new UnsupportedOperationException("not implemented");
    }
}
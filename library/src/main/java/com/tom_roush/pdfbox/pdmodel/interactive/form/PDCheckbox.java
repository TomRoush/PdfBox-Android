package com.tom_roush.pdfbox.pdmodel.interactive.form;

import com.tom_roush.pdfbox.cos.COSBase;
import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import com.tom_roush.pdfbox.pdmodel.interactive.annotation.PDAppearanceDictionary;
import com.tom_roush.pdfbox.pdmodel.interactive.annotation.PDAppearanceEntry;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
        return getValue().compareTo(getOnValue()) == 0;
    }

    /**
     * Checks the check box.
     *
     * @throws IOException if the appearance couldn't be generated.
     */
    public void check() throws IOException
    {
        setValue(getOnValue());
    }

    /**
     * Unchecks the check box.
     *
     * @throws IOException if the appearance couldn't be generated.
     */
    public void unCheck() throws IOException
    {
        setValue(COSName.Off.getName());
    }

    /**
     * Returns the fields value entry.
     *
     * @return the fields value entry.
     */
    public String getValue()
    {
        // the dictionary shall be a name object but it might not be
        // so don't assume it is.
        COSBase value = getInheritableAttribute(COSName.V);
        if (value instanceof COSName)
        {
            return ((COSName) value).getName();
        }
        else
        {
            return "";
        }
    }

    /**
     * Returns the default value, if any.
     *
     * @return the fields default value.
     */
    public String getDefaultValue()
    {
        // the dictionary shall be a name object but it might not be
        // so don't assume it is.
        COSBase value = getInheritableAttribute(COSName.DV);
        if (value instanceof COSName)
        {
            return ((COSName) value).getName();
        }
        else
        {
            return "";
        }
    }

    @Override
    public String getValueAsString()
    {
        return getValue();
    }

    /**
     * Sets the checked value of this field.
     *
     * <p>To retrieve the potential On value use {@link #getOnValue()} or
     * {@link #getOnValues()}. The Off value shall always be 'Off'.</p>
     *
     * @param value matching the On or Off state of the checkbox.
     * @throws IOException if the appearance couldn't be generated.
     * @throws IllegalArgumentException if the value is not a valid option for the checkbox.
     */
    public void setValue(String value) throws IOException
    {
        if (value.compareTo(getOnValue()) != 0 && value.compareTo(COSName.Off.getName()) != 0)
        {
            throw new IllegalArgumentException(
                value + " is not a valid option for the checkbox " + getFullyQualifiedName());
        }
        else
        {
            // Update the field value and the appearance state.
            // Both are necessary to work properly with different viewers.
            COSName name = COSName.getPDFName(value);
            dictionary.setItem(COSName.V, name);
            dictionary.setItem(COSName.AS, name);
        }

        applyChange();
    }

    /**
     * Sets the default value.
     *
     * @see #setValue(String)
     * @param value matching the On or Off state of the checkbox.
     */
    public void setDefaultValue(String value)
    {
        if (value.compareTo(getOnValue()) != 0 && value.compareTo(COSName.Off.getName()) != 0)
        {
            throw new IllegalArgumentException(
                value + " is not a valid option for the checkbox " + getFullyQualifiedName());
        }
        else
        {
            dictionary.setName(COSName.DV, value);
        }
    }

    /**
     * Get the value which sets the check box to the On state.
     *
     * <p>The On value should be 'Yes' but other values are possible
     * so we need to look for that. On the other hand the Off value shall
     * always be 'Off'. If not set or not part of the normal appearance keys
     * 'Off' is the default</p>
     *
     * @return the value setting the check box to the On state.
     * If an empty string is returned there is no appearance definition.
     * @throws IOException if the value could not be set
     */
    public String getOnValue()
    {
        PDAnnotationWidget widget = this.getWidgets().get(0);
        PDAppearanceDictionary apDictionary = widget.getAppearance();

        String onValue = "";
        if (apDictionary != null)
        {
            PDAppearanceEntry normalAppearance = apDictionary.getNormalAppearance();
            if (normalAppearance != null)
            {
                Set<COSName> entries = normalAppearance.getSubDictionary().keySet();
                for (COSName entry : entries)
                {
                    if (COSName.Off.compareTo(entry) != 0)
                    {
                        onValue = entry.getName();
                    }
                }
            }
        }
        return onValue;
    }

    /**
     * Get the values which sets the check box to the On state.
     *
     * <p>This is a convenience function to provide a similar method to
     * {@link PDRadioButton} </p>
     *
     * @return the value setting the check box to the On state.
     * If an empty List is returned there is no appearance definition.
     * @throws IOException if the value could not be set
     * @see #getOnValue()
     */
    public Set<String> getOnValues()
    {
        String onValue = getOnValue();

        if (onValue.isEmpty())
        {
            return Collections.emptySet();
        }
        else
        {
            Set<String> onValues = new HashSet<>();
            onValues.add(onValue);
            return onValues;
        }
    }
}

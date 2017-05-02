package com.tom_roush.pdfbox.pdmodel.interactive.form;

import com.tom_roush.pdfbox.cos.COSDictionary;

import java.util.Collections;
import java.util.List;

/**
 * A pushbutton is a purely interactive control that responds immediately to user
 * input without retaining a permanent value.
 *
 * @author sug
 */
public class PDPushButton extends PDButton
{
	/**
     * @see PDField#PDfield(PDAcroForm)
     *
     * @param acroForm The acroform.
     */
    public PDPushButton(PDAcroForm acroForm)
    {
        super(acroForm);
        setPushButton(true);
    }

    /**
     * Constructor.
     *
     * @param acroForm The form that this field is part of.
     * @param field the PDF object to represent as a field.
     * @param parent the parent node of the node to be created
     */
    PDPushButton(PDAcroForm acroForm, COSDictionary field, PDNonTerminalField parent)
    {
        super(acroForm, field, parent);
    }

    @Override
    public List<String> getExportValues()
    {
        return Collections.emptyList();
    }

    @Override
    public void setExportValues(List<String> values)
    {
        if (values != null && !values.isEmpty())
        {
            throw new IllegalArgumentException(
                "A PDPushButton shall not use the Opt entry in the field dictionary");
        }
    }

    @Override
    public String getValueAsString()
    {
        // PushButton fields don't support the "V" entry.
        return "";
    }
}
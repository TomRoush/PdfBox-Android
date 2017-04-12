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
    public String getDefaultValue()
    {
    	// PushButton fields don't support the "DV" entry.
    	return "";
    }

    @Override
    public void setDefaultValue(String defaultValue)
    {
    	if (defaultValue != null && !defaultValue.isEmpty())
    	{
    		throw new IllegalArgumentException("A PDPushButton shall not use the DV entry in the field dictionary");
    	}
    }

    @Override
    public List<String> getOptions()
    {
    	return Collections.emptyList();
    }

    @Override
    public void setOptions(List<String> values)
    {
    	if (values != null && !values.isEmpty())
    	{
    		throw new IllegalArgumentException("A PDPushButton shall not use the Opt entry in the field dictionary");
    	}
    }

    @Override
    public String getValue()
    {
        // PushButton fields don't support the "V" entry.
        return "";
    }

    @Override
    public void setValue(String fieldValue)
    {
    	if (fieldValue != null && !fieldValue.isEmpty())
    	{
    		throw new IllegalArgumentException("A PDPushButton shall not use the V entry in the field dictionary");
    	}
    }
}
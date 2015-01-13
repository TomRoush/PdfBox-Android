package org.apache.pdfbox.pdmodel.interactive.form;

import org.apache.pdfbox.cos.COSDictionary;

/**
 * A pushbutton is a purely interactive control that responds immediately to user
 * input without retaining a permanent value.
 *
 * @author sug
 */
public class PDPushButton extends PDButton
{

    /**
     * Constructor.
     * 
     * @param theAcroForm The form that this field is part of.
     * @param field the PDF object to represent as a field.
     * @param parentNode the parent node of the node to be created
     */
    public PDPushButton( PDAcroForm theAcroForm, COSDictionary field, PDFieldTreeNode parentNode)
    {
        super(theAcroForm, field, parentNode);
    }

    @Override
    public Object getDefaultValue()
    {
    	// PushButton fields don't support the "V" entry.
    	return null;
    }

    @Override
    public void setDefaultValue(String defaultValue)
    {
    	throw new IllegalArgumentException("A PDPushButton shall not use the DV entry in the field dictionary");
    }

    @Override
    public Object getValue()
    {
        // PushButton fields don't support the "V" entry.
        return null;
    }
    
    @Override
    public void setValue(String fieldValue)
    {
    	throw new IllegalArgumentException("A PDPushButton shall not use the V entry in the field dictionary");
    }
}
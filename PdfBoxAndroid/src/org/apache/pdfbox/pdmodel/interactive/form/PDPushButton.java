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
    public Object getValue()
    {
        // PushButton fields don't support the "V" entry.
        return null;
    }
}

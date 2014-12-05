package org.apache.pdfbox.pdmodel.interactive.form;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;

/**
 * A combo box consisting of a drop-down list.
 * May be accompanied by an editable text box in which non-predefined values may be entered.
 * @author John Hewson
 */
public final class PDComboBox extends PDChoice
{
    /**
     *  Ff-flag.
     */
    private static final int FLAG_EDIT = 1 << 18;

    /**
     * Constructor.
     * 
     * @param acroForm The form that this field is part of.
     * @param field the PDF object to represent as a field.
     * @param parentNode the parent node of the node to be created
     */
    public PDComboBox(PDAcroForm acroForm, COSDictionary field, PDFieldTreeNode parentNode)
    {
        super(acroForm, field, parentNode);
    }

    /**
     * Determines if Edit is set.
     * 
     * @return true if the combo box shall include an editable text box as well as a drop-down list.
     */
    public boolean isEdit()
    {
        return getDictionary().getFlag( COSName.FF, FLAG_EDIT );
    }

    /**
     * Set the Edit bit.
     *
     * @param edit The value for Edit.
     */
    public void setEdit( boolean edit )
    {
        getDictionary().setFlag( COSName.FF, FLAG_EDIT, edit );
    }

    /**
     * setValue sets the entry "V" to the given value.
     * 
     * @param value the value
     * 
     */
    public void setValue(String value)
    {
        if ((getFieldFlags() & FLAG_EDIT) != 0)
        {
            throw new IllegalArgumentException("The combo box isn't editable.");
        }
        super.setValue(value);
    }

    /**
     * setValue sets the entry "V" to the given value.
     * 
     * @param value the value
     */
    public void setValue(String[] value)
    {
    	if ((getFieldFlags() & FLAG_EDIT) != 0)
    	{
    		throw new IllegalArgumentException("The combo box isn't editable.");
    	}
    	super.setValue(value);
    }
}

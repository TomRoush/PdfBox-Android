package com.tom_roush.pdfbox.pdmodel.interactive.form;

import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSName;

/**
 * A combo box consisting of a drop-down list.
 * May be accompanied by an editable text box in which non-predefined values may be entered.
 *
 * @author John Hewson
 */
public final class PDComboBox extends PDChoice
{
    /**
     * Ff-flag.
     */
    private static final int FLAG_EDIT = 1 << 18;

    /**
     * @param theAcroForm The acroform.
     * @see PDFieldTreeNode#PDFieldTreeNode(PDAcroForm)
     */
    public PDComboBox(PDAcroForm theAcroForm)
    {
        super(theAcroForm);
        setCombo(true);
    }

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
        return getCOSObject().getFlag(COSName.FF, FLAG_EDIT);
    }

    /**
     * Set the Edit bit.
     *
     * @param edit The value for Edit.
     */
    public void setEdit(boolean edit)
    {
        getCOSObject().setFlag(COSName.FF, FLAG_EDIT, edit);
    }

    /**
     * Sets the field value - the 'V' key.
     *
     * @param value the value
     */
    @Override
    public void setValue(String value)
    {
        if (value != null)
        {
            // check if the options contain the value to be set is
            // only necessary if the edit flag has not been set.
            // If the edit flag has been set the field allows a custom value.
            if (!isEdit() && getOptions().indexOf(value) == -1)
            {
                throw new IllegalArgumentException("The list box does not contain the given value.");
            }
            else
            {
                getCOSObject().setString(COSName.V, value);
                // remove I key for single valued choice field
                setSelectedOptionsIndex(null);
            }
        }
        else
        {
            getCOSObject().removeItem(COSName.V);
        }
        // TODO create/update appearance
    }
}

package com.tom_roush.pdfbox.pdmodel.interactive.form;

import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSName;

import java.io.IOException;
import java.util.List;

/**
 * A combo box consisting of a drop-down list.
 * May be accompanied by an editable text box in which non-predefined values may be entered.
 *
 * @author John Hewson
 */
public final class PDComboBox extends PDChoice
{
    private static final int FLAG_EDIT = 1 << 18;

    /**
     * @see PDField#PDField(PDAcroForm)
     *
     * @param acroform The acroform.
     */
    public PDComboBox(PDAcroForm acroform)
    {
        super(acroform);
        setCombo(true);
    }

    /**
     * Constructor.
     *
     * @param acroForm The form that this field is part of.
     * @param field the PDF object to represent as a field.
     * @param parent the parent node of the node
     */
    PDComboBox(PDAcroForm acroForm, COSDictionary field, PDNonTerminalField parent)
    {
        super(acroForm, field, parent);
    }

    /**
     * Determines if Edit is set.
     *
     * @return true if the combo box shall include an editable text box as well as a drop-down list.
     */
    public boolean isEdit()
    {
        return dictionary.getFlag(COSName.FF, FLAG_EDIT);
    }

    /**
     * Set the Edit bit.
     *
     * @param edit The value for Edit.
     */
    public void setEdit(boolean edit)
    {
        dictionary.setFlag(COSName.FF, FLAG_EDIT, edit);
    }

    @Override
    void constructAppearances() throws IOException
    {
        AppearanceGeneratorHelper apHelper;
        apHelper = new AppearanceGeneratorHelper(this);
        List<String> values = getValue();

        if (!values.isEmpty())
        {
            apHelper.setAppearanceValue(values.get(0));
        }
        else
        {
            apHelper.setAppearanceValue("");
        }
    }
}

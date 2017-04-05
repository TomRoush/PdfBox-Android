package com.tom_roush.pdfbox.pdmodel.interactive.form;

import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSInteger;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.pdmodel.interactive.action.PDFormFieldAdditionalActions;

import java.io.IOException;

/**
 * A field in an interactive form.
 * Fields may be one of four types: button, text, choice, or signature.
 *
 * @author sug
 */
public abstract class PDField extends PDFieldTreeNode
{
    /**
     * Constructor.
     *
     * @param theAcroForm The form that this field is part of.
     */
    protected PDField(PDAcroForm theAcroForm)
    {
        super(theAcroForm);
    }

    /**
     * Constructor.
     *
     * @param theAcroForm The form that this field is part of.
     * @param field the PDF object to represent as a field.
     * @param parentNode the parent node of the node to be created
     */
    protected PDField(PDAcroForm theAcroForm, COSDictionary field, PDFieldTreeNode parentNode)
    {
        super(theAcroForm, field, parentNode);
    }

    /**
     * Set the actions of the field.
     *
     * @param actions The field actions.
     */
    public void setActions(PDFormFieldAdditionalActions actions)
    {
        getCOSObject().setItem(COSName.AA, actions);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getFieldFlags()
    {
        int retval = 0;
        COSInteger ff = (COSInteger) getCOSObject().getDictionaryObject(COSName.FF);
        if (ff != null)
        {
            retval = ff.intValue();
        }
        else if (getParent() != null)
        {
            retval = getParent().getFieldFlags();
        }
        return retval;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFieldType()
    {
        String fieldType = getCOSObject().getNameAsString(COSName.FT);
        if (fieldType == null && getParent() != null)
        {
            fieldType = getParent().getFieldType();
        }
        return fieldType;
    }

    /**
     * Update the fields appearance stream.
     * <p>
     * The fields appearance stream needs to be updated to reflect the new field
     * value. This will be done only if the NeedAppearances flag has not been set.
     */
    protected void updateFieldAppearances() throws IOException
    {
        if (!getAcroForm().isNeedAppearances())
        {
            AppearanceGenerator.generateFieldAppearances(this);
        }
    }
}

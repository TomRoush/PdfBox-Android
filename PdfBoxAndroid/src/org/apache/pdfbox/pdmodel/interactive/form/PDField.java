package org.apache.pdfbox.pdmodel.interactive.form;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.interactive.action.PDFormFieldAdditionalActions;
import org.apache.pdfbox.util.appearance.AppearanceGenerator;

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
	 * This will return a string representation of this field.
	 * 
	 * @return A string representation of this field.
	 */
	@Override
	public String toString()
	{
		return "" + getDictionary().getDictionaryObject(COSName.V);
	}

	/**
	 * Get the additional actions for this field. This will return null if there are no additional actions for this
	 * field.
	 * 
	 * @return The actions of the field.
	 */
	public PDFormFieldAdditionalActions getActions()
	{
		COSDictionary aa = (COSDictionary) getDictionary().getDictionaryObject(COSName.AA);
		PDFormFieldAdditionalActions retval = null;
		if (aa != null)
		{
			retval = new PDFormFieldAdditionalActions(aa);
		}
		return retval;
	}

	/**
	 * Set the actions of the field.
	 * 
	 * @param actions The field actions.
	 */
	public void setActions(PDFormFieldAdditionalActions actions)
	{
		getDictionary().setItem(COSName.AA, actions);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getFieldFlags()
	{
		int retval = 0;
		COSInteger ff = (COSInteger) getDictionary().getDictionaryObject(COSName.FF);
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
		String fieldType = getDictionary().getNameAsString(COSName.FT);
		if (fieldType == null && getParent() != null)
		{
			fieldType = getParent().getFieldType();
		}
		return fieldType;
	}

	/**
	 * Update the fields appearance stream.
	 *
	 * The fields appearance stream needs to be updated to reflect the new field
	 * value. This will be done only if the NeedAppearances flag has not been set.
	 */
	protected void updateFieldAppearances()
	{
		if (!getAcroForm().isNeedAppearances())
		{
			AppearanceGenerator.generateFieldAppearances(this);
		}
	}
}

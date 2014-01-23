package org.apache.pdfboxandroid.pdmodel.interactive.form;

import org.apache.pdfboxandroid.cos.COSDictionary;

/**
 * This holds common functionality for check boxes and radio buttons.
 *
 * @author sug
 * @version $Revision: 1.4 $
 */
public abstract class PDChoiceButton extends PDField {
	/**
     * @see PDField#PDField(PDAcroForm,org.apache.pdfbox.cos.COSDictionary)
     *
     * @param theAcroForm The acroForm for this field.
     * @param field The field for this button.
     */
    public PDChoiceButton( PDAcroForm theAcroForm, COSDictionary field)
    {
        super(theAcroForm, field);
    }
}

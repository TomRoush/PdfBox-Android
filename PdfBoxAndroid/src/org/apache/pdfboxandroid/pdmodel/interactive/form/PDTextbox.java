package org.apache.pdfboxandroid.pdmodel.interactive.form;

import org.apache.pdfboxandroid.cos.COSDictionary;

/**
 * A class for handling the PDF field as a textbox.
 *
 * @author sug
 * 
 */
public class PDTextbox extends PDVariableText {
	/**
     * @see org.apache.pdfbox.pdmodel.interactive.form.PDField#PDField(PDAcroForm,COSDictionary)
     *
     * @param theAcroForm The acroForm for this field.
     * @param field The field's dictionary.
     */
    public PDTextbox( PDAcroForm theAcroForm, COSDictionary field)
    {
        super( theAcroForm, field);
    }
}

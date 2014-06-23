package org.apache.pdfboxandroid.pdmodel.interactive.form;

import java.io.IOException;

import org.apache.pdfboxandroid.cos.COSDictionary;

/**
 * This class represents a form field with an unknown type.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.4 $
 */
public class PDUnknownField extends PDField {
	/**
     * @see org.apache.pdfbox.pdmodel.interactive.form.PDField#PDField(PDAcroForm, COSDictionary)
     *
     * @param theAcroForm The acroForm for this field.
     * @param field The field's dictionary.
     */
    public PDUnknownField( PDAcroForm theAcroForm, COSDictionary field)
    {
        super( theAcroForm, field);
    }
    
    /**
     * {@inheritDoc}
     */
    public void setValue(String value) throws IOException
    {
        //do nothing
    }
}

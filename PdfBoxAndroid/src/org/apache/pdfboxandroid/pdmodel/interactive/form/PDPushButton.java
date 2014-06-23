package org.apache.pdfboxandroid.pdmodel.interactive.form;

import java.io.IOException;

import org.apache.pdfboxandroid.cos.COSDictionary;
import org.apache.pdfboxandroid.cos.COSName;
import org.apache.pdfboxandroid.cos.COSString;

/**
 * A class for handling the PDF field as a PDPushButton.
 *
 * @author sug
 * @version $Revision: 1.3 $
 */
public class PDPushButton extends PDField {
	/**
     * @see org.apache.pdfbox.pdmodel.field.PDField#COSField(org.apache.pdfbox.cos.COSDictionary)
     *
     * @param theAcroForm The acroForm for this field.
     * @param field The field for this push button.
     */
    public PDPushButton( PDAcroForm theAcroForm, COSDictionary field)
    {
        super(theAcroForm, field);
    }
    
    /**
     * @see as.interactive.pdf.form.cos.COSField#setValue(java.lang.String)
     *
     * @param value The new value for the field.
     *
     * @throws IOException If there is an error creating the appearance stream.
     */
    public void setValue(String value) throws IOException
    {
        COSString fieldValue = new COSString(value);
        getDictionary().setItem( COSName.getPDFName( "V" ), fieldValue );
        getDictionary().setItem( COSName.getPDFName( "DV" ), fieldValue );
    }
}

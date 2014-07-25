package org.apache.pdfbox.pdmodel.interactive.form;

import java.io.IOException;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;

/**
 * A class for handling the PDF field as a PDPushButton.
 *
 * @author sug
 * @version $Revision: 1.3 $
 */
public class PDPushButton extends PDField
{

    /**
     * @see PDField#PDField(PDAcroForm, COSDictionary)
     *
     * @param theAcroForm The acroForm for this field.
     * @param field The field for this push button.
     */
    public PDPushButton( PDAcroForm theAcroForm, COSDictionary field)
    {
        super(theAcroForm, field);
    }

    /**
     * @see PDField#setValue(java.lang.String)
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

    /**
     * getValue gets the fields value to as a string.
     *
     * @return The string value of this field.
     *
     * @throws IOException If there is an error getting the value.
     */
    public String getValue() throws IOException
    {
        return getDictionary().getString( "V" );
    }
}

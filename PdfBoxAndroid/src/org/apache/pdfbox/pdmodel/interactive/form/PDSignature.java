package org.apache.pdfbox.pdmodel.interactive.form;

import java.io.IOException;

import org.apache.pdfbox.cos.COSDictionary;

/**
 * A class for handling the PDF field as a signature.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.5 $
 * 
 * @deprecated Use {@link PDSignatureField} instead (see PDFBOX-1513).
 */
public class PDSignature extends PDField
{

    /**
     * @see PDField#PDField(PDAcroForm,COSDictionary)
     *
     * @param theAcroForm The acroForm for this field.
     * @param field The dictionary for the signature.
     */
    public PDSignature( PDAcroForm theAcroForm, COSDictionary field)
    {
        super(theAcroForm,field);
        throw new RuntimeException( "The usage of " + getClass().getName() 
                + " is deprecated. Please use " + PDSignatureField.class.getName() + " instead (see PDFBOX-1513)" );
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
        throw new RuntimeException( "Not yet implemented" );
    }

    /**
     * @see PDField#setValue(java.lang.String)
     *
     * @return The string value of this field.
     *
     * @throws IOException If there is an error creating the appearance stream.
     */
    public String getValue() throws IOException
    {
        throw new RuntimeException( "Not yet implemented" );
    }

    /**
     * Return a string rep of this object.
     *
     * @return A string rep of this object.
     */
    public String toString()
    {
        return "PDSignature";
    }
}

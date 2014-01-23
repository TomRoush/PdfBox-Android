package org.apache.pdfboxandroid.pdmodel.interactive.form;

import java.io.IOException;

import org.apache.pdfboxandroid.cos.COSDictionary;
import org.apache.pdfboxandroid.cos.COSName;
import org.apache.pdfboxandroid.pdmodel.interactive.annotation.PDAnnotationWidget;

/**
 * A class for handling the PDF field as a signature.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @author Thomas Chojecki
 * @version $Revision: 1.5 $
 */
public class PDSignatureField extends PDField {
	/**
     * @see PDField#PDField(PDAcroForm,COSDictionary)
     *
     * @param theAcroForm The acroForm for this field.
     * @param field The dictionary for the signature.
     * @throws IOException If there is an error while resolving partital name for the signature field
     */
    public PDSignatureField( PDAcroForm theAcroForm, COSDictionary field) throws IOException
    {
        super(theAcroForm,field);
        // dirty hack to avoid npe caused through getWidget() method
        getDictionary().setItem( COSName.TYPE, COSName.ANNOT );
        getDictionary().setName( COSName.SUBTYPE, PDAnnotationWidget.SUB_TYPE);
    }
    
    /**
     * @see PDField#setValue(java.lang.String)
     *
     * @param value The new value for the field.
     *
     * @throws IOException If there is an error creating the appearance stream.
     * @deprecated use setSignature(PDSignature) instead
     */
    @Override
    @Deprecated
    public void setValue(String value) throws IOException
    {
        throw new RuntimeException( "Can't set signature as String, use setSignature(PDSignature) instead" );
    }
}

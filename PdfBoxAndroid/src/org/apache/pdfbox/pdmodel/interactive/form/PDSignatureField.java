package org.apache.pdfbox.pdmodel.interactive.form;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;

/**
 * A class for handling the PDF field as a signature.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @author Thomas Chojecki
 * @version $Revision: 1.5 $
 */
public class PDSignatureField extends PDField
{

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
     * @see PDField#PDField(PDAcroForm)
     *
     * @param theAcroForm The acroForm for this field.
     * @throws IOException If there is an error while resolving partial name for the signature field
     *         or getting the widget object.
     */
    public PDSignatureField( PDAcroForm theAcroForm) throws IOException
    {
        super( theAcroForm );
        getDictionary().setItem(COSName.FT, COSName.SIG);
        getWidget().setLocked(true);
        getWidget().setPrinted(true);
        setPartialName(generatePartialName());
        getDictionary().setItem( COSName.TYPE, COSName.ANNOT );
        getDictionary().setName( COSName.SUBTYPE, PDAnnotationWidget.SUB_TYPE);
    }
    
    /**
     * Generate a unique name for the signature.
     * @return
     * @throws IOException If there is an error while getting the list of fields.
     */
    private String generatePartialName() throws IOException
    {
      PDAcroForm acroForm = getAcroForm();
      List fields = acroForm.getFields();
      
      String fieldName = "Signature";
      int i = 1;
      
      Set<String> sigNames = new HashSet<String>();
      
      for ( Object object : fields )
      {
        if(object instanceof PDSignatureField)
        {
          sigNames.add(((PDSignatureField)object).getPartialName());
        }
      }

      while(sigNames.contains(fieldName+i))
      {
        ++i;
      }
      return fieldName+i;
    }

}

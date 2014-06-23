package org.apache.pdfboxandroid.pdmodel.interactive.form;

import java.io.IOException;
import java.util.List;

import org.apache.pdfboxandroid.cos.COSDictionary;
import org.apache.pdfboxandroid.cos.COSName;

/**
 * A class for handling the PDF field as a Radio Collection.
 * This class automatically keeps track of the child radio buttons
 * in the collection.
 *
 * @see PDCheckbox
 * @author sug
 * @version $Revision: 1.13 $
 */
public class PDRadioCollection extends PDChoiceButton {
	/**
     * @param theAcroForm The acroForm for this field.
     * @param field The field that makes up the radio collection.
     *
     * {@inheritDoc}
     */
    public PDRadioCollection( PDAcroForm theAcroForm, COSDictionary field)
    {
        super(theAcroForm,field);
    }
    
    /**
     * This setValue method iterates the collection of radiobuttons
     * and checks or unchecks each radiobutton according to the
     * given value.
     * If the value is not represented by any of the radiobuttons,
     * then none will be checked.
     *
     * {@inheritDoc}
     */
    public void setValue(String value) throws IOException
    {
        getDictionary().setString( COSName.V, value );
        List kids = getKids();
        for (int i = 0; i < kids.size(); i++)
        {
            PDField field = (PDField)kids.get(i);
            if ( field instanceof PDCheckbox )
            {
                PDCheckbox btn = (PDCheckbox)field;
                if( btn.getOnValue().equals(value) )
                {
                    btn.check();
                }
                else
                {
                    btn.unCheck();
                }
            }
        }
    }
}

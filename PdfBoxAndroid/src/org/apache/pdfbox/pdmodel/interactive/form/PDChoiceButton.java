package org.apache.pdfbox.pdmodel.interactive.form;

import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.common.COSArrayList;

/**
 * This holds common functionality for check boxes and radio buttons.
 *
 * @author sug
 * @version $Revision: 1.4 $
 */
public abstract class PDChoiceButton extends PDField
{

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

    /**
     * This will get the option values "Opt" entry of the pdf button.
     *
     * @return A list of java.lang.String values.
     */
    public List getOptions()
    {
        List retval = null;
        COSArray array = (COSArray)getDictionary().getDictionaryObject( COSName.getPDFName( "Opt" ) );
        if( array != null )
        {
            List strings = new ArrayList();
            for( int i=0; i<array.size(); i++ )
            {
                strings.add( ((COSString)array.getObject( i )).getString() );
            }
            retval = new COSArrayList( strings, array );
        }
        return retval;
    }

    /**
     * This will will set the list of options for this button.
     *
     * @param options The list of options for the button.
     */
    public void setOptions( List options )
    {
        getDictionary().setItem(
            COSName.getPDFName( "Opt" ),
            COSArrayList.converterToCOSArray( options ) );
    }
}

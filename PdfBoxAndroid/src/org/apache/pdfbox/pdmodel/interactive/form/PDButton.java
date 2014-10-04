package org.apache.pdfbox.pdmodel.interactive.form;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.common.COSArrayList;

import java.util.ArrayList;
import java.util.List;

/**
 * A button field represents an interactive control on the screen
 * that the user can manipulate with the mouse.
 *
 * @author sug
 */
public abstract class PDButton extends PDField
{
    /**
     * A Ff flag.
     */
    public static final int FLAG_NO_TOGGLE_TO_OFF = 1 << 14;
    /**
     * A Ff flag.
     */
    public static final int FLAG_RADIO = 1 << 15;
    /**
     * A Ff flag.
     */
    public static final int FLAG_PUSHBUTTON = 1 << 16;
    /**
     * A Ff flag.
     */
    public static final int FLAG_RADIOS_IN_UNISON = 1 << 25;

    /**
     * Constructor.
     * 
     * @param theAcroForm The form that this field is part of.
     * @param field the PDF object to represent as a field.
     * @param parentNode the parent node of the node to be created
     */
    protected PDButton(PDAcroForm acroForm, COSDictionary field, PDFieldTreeNode parentNode)
    {
        super(acroForm, field, parentNode);
    }

    /**
     * This will get the option values "Opt" entry of the pdf button.
     *
     * @return A list of java.lang.String values.
     */
    public List<String> getOptions()
    {
        List<String> retval = null;
        COSArray array = (COSArray)getDictionary().getDictionaryObject( COSName.OPT );
        if( array != null )
        {
            List<String> strings = new ArrayList<String>();
            for( int i=0; i<array.size(); i++ )
            {
                strings.add( ((COSString)array.getObject( i )).getString() );
            }
            retval = new COSArrayList<String>( strings, array );
        }
        return retval;
    }

    /**
     * This will will set the list of options for this button.
     *
     * @param options The list of options for the button.
     */
    public void setOptions( List<String> options )
    {
        getDictionary().setItem(COSName.OPT, COSArrayList.converterToCOSArray( options ) );
    }
    
    @Override
    public Object getDefaultValue()
    {
        // Button fields don't support the "DV" entry.
        return null;
    }

    @Override
    public void setDefaultValue(Object value)
    {
        // Button fields don't support the "DV" entry.
        throw new RuntimeException( "Button fields don't support the \"DV\" entry." );
    }

}

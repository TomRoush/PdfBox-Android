package org.apache.pdfboxandroid.pdmodel.interactive.form;

import java.io.IOException;

import org.apache.pdfboxandroid.cos.COSDictionary;
import org.apache.pdfboxandroid.cos.COSName;
import org.apache.pdfboxandroid.cos.COSNumber;
import org.apache.pdfboxandroid.cos.COSString;
import org.apache.pdfboxandroid.util.BitFlagHelper;

/**
 * A class for handling PDF fields that display text.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.7 $
 */
public abstract class PDVariableText extends PDField {
	/**
     * A Q value.
     */
    public static final int QUADDING_LEFT = 0;

    /**
     * A Q value.
     */
    public static final int QUADDING_CENTERED = 1;

    /**
     * A Q value.
     */
    public static final int QUADDING_RIGHT = 2;
	
	/**
     * A Ff flag.
     */
    public static final int FLAG_MULTILINE = 1 << 12;
    /**
     * A Ff flag.
     */
    public static final int FLAG_DO_NOT_SCROLL = 1 << 23;
	
	/**
     * DA    Default appearance.
     */
    private COSString da;
    
    private PDAppearance appearance;
	
	/**
     * @see org.apache.pdfbox.pdmodel.interactive.form.PDField#PDField(PDAcroForm,COSDictionary)
     *
     * @param theAcroForm The acroForm for this field.
     * @param field The field's dictionary.
     */
    public PDVariableText( PDAcroForm theAcroForm, COSDictionary field)
    {
        super( theAcroForm, field);
        da = (COSString) field.getDictionaryObject(COSName.DA);
    }
    
    /**
     * @see org.apache.pdfbox.pdmodel.interactive.form.PDField#setValue(java.lang.String)
     *
     * @param value The new value for this text field.
     *
     * @throws IOException If there is an error calculating the appearance stream.
     */
    public void setValue(String value) throws IOException
    {
        COSString fieldValue = new COSString(value);
        getDictionary().setItem( COSName.V, fieldValue );

        //hmm, not sure what the case where the DV gets set to the field
        //value, for now leave blank until we can come up with a case
        //where it needs to be in there
        //getDictionary().setItem( COSName.getPDFName( "DV" ), fieldValue );
        if(appearance == null)
        {
            this.appearance = new PDAppearance( getAcroForm(), this );
        }
        appearance.setAppearanceValue(value);
    }
    
    /**
     * @return the DA element of the dictionary object
     */
    protected COSString getDefaultAppearance()
    {
        return da;
    }
    
    /**
     * @return true if the field is multiline
     */
    public boolean isMultiline()
    {
        return BitFlagHelper.getFlag( getDictionary(), COSName.FF, FLAG_MULTILINE );
    }
    
    /**
     * @return true if the field is not suppose to scroll.
     */
    public boolean doNotScroll()
    {
        return BitFlagHelper.getFlag( getDictionary(), COSName.FF, FLAG_DO_NOT_SCROLL );
    }
    
    /**
     * This will get the 'quadding' or justification of the text to be displayed.
     * 0 - Left(default)<br/>
     * 1 - Centered<br />
     * 2 - Right<br />
     * Please see the QUADDING_CONSTANTS.
     *
     * @return The justification of the text strings.
     */
    public int getQ()
    {
        int retval = 0;
        COSNumber number = (COSNumber)getDictionary().getDictionaryObject( COSName.Q );
        if( number != null )
        {
            retval = number.intValue();
        }
        return retval;
    }
}

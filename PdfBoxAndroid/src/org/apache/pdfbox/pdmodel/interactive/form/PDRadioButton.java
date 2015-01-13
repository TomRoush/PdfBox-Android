package org.apache.pdfbox.pdmodel.interactive.form;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

/**
 * Radio button fields contain a set of related buttons that can each be on or off.
 *
 * @author sug
 */
public final class PDRadioButton extends PDButton
{

    /**
     * Constructor.
     * 
     * @param theAcroForm The form that this field is part of.
     * @param field the PDF object to represent as a field.
     * @param parentNode the parent node of the node to be created
     */
    public PDRadioButton(PDAcroForm theAcroForm, COSDictionary field, PDFieldTreeNode parentNode)
    {
        super(theAcroForm, field, parentNode);
    }

    /**
     * From the PDF Spec <br/>
     * If set, a group of radio buttons within a radio button field that use the same value for the on state will turn
     * on and off in unison; that is if one is checked, they are all checked. If clear, the buttons are mutually
     * exclusive (the same behavior as HTML radio buttons).
     *
     * @param radiosInUnison The new flag for radiosInUnison.
     */
    public void setRadiosInUnison(boolean radiosInUnison)
    {
        getDictionary().setFlag(COSName.FF, FLAG_RADIOS_IN_UNISON, radiosInUnison);
    }

    /**
     *
     * @return true If the flag is set for radios in unison.
     */
    public boolean isRadiosInUnison()
    {
        return getDictionary().getFlag(COSName.FF, FLAG_RADIOS_IN_UNISON);
    }
    
    @Override
    public COSName getDefaultValue() throws IOException
    {
    	COSBase attribute = getInheritableAttribute(COSName.DV);
    	if (attribute instanceof COSName)
    	{
    		return (COSName) attribute;
    	}
    	else
    	{
    		throw new IOException("Expected a COSName entry but got " + attribute.getClass().getName());
    	}
    }
    
    /**
     * Set the fields default value.
     * 
     * The field value holds a name object which is corresponding to the
     * appearance state representing the corresponding appearance
     * from the appearance directory.
     * 
     * The default value is used to represent the initial state of the
     * checkbox or to revert when resetting the form.
     * 
     * @param defaultValue the String to set the field value.
     */
    public void setDefaultValue(String defaultValue)
    {
    	if (defaultValue == null)
    	{
    		removeInheritableAttribute(COSName.DV);
    	}
    	else
    	{
    		setInheritableAttribute(COSName.DV, new COSString(defaultValue));
    	}
    }

    @Override
    public String getValue() throws IOException
    {
    	COSBase attribute = getInheritableAttribute(COSName.V);
    	
    	if (attribute instanceof COSName)
    	{
    		return ((COSName) attribute).getName();
    	}
    	else
    	{
    		throw new IOException("Expected a COSName entry but got " + attribute.getClass().getName());
    	}
    }

    /**
     * Set the field value.
     * 
     * The field value holds a name object which is corresponding to the 
     * appearance state of the child field being in the on state.
     * 
     * The default value is Off.
     * 
     * @param fieldValue the COSName object to set the field value.
     */
    @Override
    public void setValue(String fieldValue)
    {
        if (fieldValue == null)
        {
        	removeInheritableAttribute(COSName.V);
        }
        else
        {
        	setInheritableAttribute(COSName.V, COSName.getPDFName(fieldValue));
            List<COSObjectable> kids = getKids();
            for (COSObjectable kid : kids)
            {
                if (kid instanceof PDCheckbox)
                {
                    PDCheckbox btn = (PDCheckbox) kid;
                    if (btn.getOnValue().equals(fieldValue))
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
}
package org.apache.pdfbox.pdmodel.interactive.form;

import java.io.IOException;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;

/**
 * A check box toggles between two states, on and off.
 *
 * @author Ben Litchfield
 * @author sug
 */
public final class PDCheckbox extends PDButton
{
	/**
	 * @see PDFieldTreeNode#PDFieldTreeNode(PDAcroForm)
	 *
	 * @param theAcroForm The acroform.
	 */
	public PDCheckbox(PDAcroForm theAcroForm)
	{
		super( theAcroForm );
	}
	
    /**
     * Constructor.
     * 
     * @param theAcroForm The form that this field is part of.
     * @param field the PDF object to represent as a field.
     * @param parentNode the parent node of the node to be created
     */
    public PDCheckbox( PDAcroForm theAcroForm, COSDictionary field, PDFieldTreeNode parentNode)
    {
        super( theAcroForm, field, parentNode);
    }

    /**
     * This will tell if this radio button is currently checked or not.
     *
     * @return true If the radio button is checked.
     * @throws IOException
     */
    public boolean isChecked() throws IOException
    {
    	String onValue = getOnValue();
    	String fieldValue = null;
    	try
    	{
    		fieldValue = getValue();
    	}
    	catch (IOException e)
    	{
    		// getting there means that the field value
    		// doesn't have a supported type.
    		// Ignoring as that will also mean that the field is not checked.
    		// Setting the value explicitly as Code Analysis (Sonar) doesn't like
    		// empty catch blocks.
    		return false;
    	}
    	COSName radioValue = (COSName)getDictionary().getDictionaryObject( COSName.AS );
    	if( radioValue != null && fieldValue != null && radioValue.getName().equals( onValue ) )
    	{
    		return true;
    	}

    	return false;
    }

    /**
     * Checks the check box.
     */
    public void check()
    {
    	String onValue = getOnValue();
    	setValue(onValue);
    	getDictionary().setItem(COSName.AS, COSName.getPDFName(onValue));
    }

    /**
     * Unchecks the check box.
     */
    public void unCheck()
    {
        getDictionary().setItem(COSName.AS, PDButton.OFF);
    }

    /**
     * This will get the value assigned to the OFF state.
     *
     * @return The value of the check box.
     */
    public String getOffValue()
    {
        return PDButton.OFF.getName();
    }

    /**
     * This will get the value assigned to the ON state.
     *
     * @return The value of the check box.
     */
    public String getOnValue()
    {
        COSDictionary ap = (COSDictionary) getDictionary().getDictionaryObject(COSName.AP);
        COSBase n = ap.getDictionaryObject(COSName.N);

        //N can be a COSDictionary or a COSStream
        if( n instanceof COSDictionary )
        {
            for( COSName key :((COSDictionary)n).keySet() )
            {
                if( !key.equals( PDButton.OFF) )
                {
                    return key.getName();
                }
            }
        }
        return "";
    }

    @Override
    public String getValue() throws IOException
    {
    	COSBase attribute = getInheritableAttribute(COSName.V);

    	if (attribute == null)
    	{
    		return "";
    	}
    	else if (attribute instanceof COSName)
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
     * appearance state representing the corresponding appearance 
     * from the appearance directory.
     * 
     * The default value is Off.
     * 
     * @param value the new field value value.
     */
    public void setValue(String value)
    {
        if (value == null)
        {
            getDictionary().removeItem(COSName.V);
            getDictionary().setItem( COSName.AS, PDButton.OFF );
        }
        else
        {
        	COSName nameValue = COSName.getPDFName(value);
        	getDictionary().setItem(COSName.V, nameValue);
        	getDictionary().setItem( COSName.AS, nameValue);
        }
    }
    
}

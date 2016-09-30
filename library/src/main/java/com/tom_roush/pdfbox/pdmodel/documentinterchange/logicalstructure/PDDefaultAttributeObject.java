package org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;

/**
 * A default attribute object.
 * 
 * @author Johannes Koch
 */
public class PDDefaultAttributeObject extends PDAttributeObject
{

    /**
     * Default constructor.
     */
    public PDDefaultAttributeObject()
    {
    }

    /**
     * Creates a default attribute object with a given dictionary.
     * 
     * @param dictionary the dictionary
     */
    public PDDefaultAttributeObject(COSDictionary dictionary)
    {
        super(dictionary);
    }


    /**
     * Gets the attribute names.
     * 
     * @return the attribute names
     */
    public List<String> getAttributeNames()
    {
        List<String> attrNames = new ArrayList<String>();
        for (Entry<COSName, COSBase> entry : this.getCOSDictionary().entrySet())
        {
            COSName key = entry.getKey();
            if (!COSName.O.equals(key))
            {
                attrNames.add(key.getName());
            }
        }
        return attrNames;
    }

    /**
     * Gets the attribute value for a given name.
     * 
     * @param attrName the given attribute name
     * @return the attribute value for a given name
     */
    public COSBase getAttributeValue(String attrName)
    {
        return this.getCOSDictionary().getDictionaryObject(attrName);
    }

    /**
     * Gets the attribute value for a given name.
     * 
     * @param attrName the given attribute name
     * @param defaultValue the default value
     * @return the attribute value for a given name
     */
    protected COSBase getAttributeValue(String attrName, COSBase defaultValue)
    {
        COSBase value = this.getCOSDictionary().getDictionaryObject(attrName);
        if (value == null)
        {
            return defaultValue;
        }
        return value;
    }

    /**
     * Sets an attribute.
     * 
     * @param attrName the attribute name
     * @param attrValue the attribute value
     */
    public void setAttribute(String attrName, COSBase attrValue)
    {
        COSBase old = this.getAttributeValue(attrName);
        this.getCOSDictionary().setItem(COSName.getPDFName(attrName), attrValue);
        this.potentiallyNotifyChanged(old, attrValue);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder().append(super.toString())
            .append(", attributes={");
        Iterator<String> it = this.getAttributeNames().iterator();
        while (it.hasNext())
        {
            String name = it.next();
            sb.append(name).append('=').append(this.getAttributeValue(name));
            if (it.hasNext())
            {
                sb.append(", ");
            }
        }
        return sb.append('}').toString();
    }

}

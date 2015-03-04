package org.apache.pdfbox.pdmodel.graphics.optionalcontent;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.documentinterchange.markedcontent.PDPropertyList;

/**
 * An optional content group (OCG).
 */
public class PDOptionalContentGroup extends PDPropertyList
{

    /**
     * Creates a new optional content group (OCG).
     * @param name the name of the content group
     */
    public PDOptionalContentGroup(String name)
    {
        this.dict.setItem(COSName.TYPE, COSName.OCG);
        setName(name);
    }

    /**
     * Creates a new instance based on a given {@link COSDictionary}.
     * @param dict the dictionary
     */
    public PDOptionalContentGroup(COSDictionary dict)
    {
    	super(dict);
        if (!dict.getItem(COSName.TYPE).equals(COSName.OCG))
        {
            throw new IllegalArgumentException(
                    "Provided dictionary is not of type '" + COSName.OCG + "'");
        }
    }

    /**
     * Returns the name of the optional content group.
     * @return the name
     */
    public String getName()
    {
        return dict.getString(COSName.NAME);
    }

    /**
     * Sets the name of the optional content group.
     * @param name the name
     */
    public void setName(String name)
    {
        dict.setString(COSName.NAME, name);
    }

    //TODO Add support for "Intent" and "Usage"

    @Override
    public String toString()
    {
        return super.toString() + " (" + getName() + ")";
    }

}

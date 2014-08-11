package org.apache.pdfbox.pdmodel.graphics.optionalcontent;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

/**
 * This class represents an optional content group (OCG).
 *
 * @since PDF 1.5
 * @version $Revision$
 */
public class PDOptionalContentGroup implements COSObjectable
{

    private COSDictionary ocg;

    /**
     * Creates a new optional content group (OCG).
     * @param name the name of the content group
     */
    public PDOptionalContentGroup(String name)
    {
        this.ocg = new COSDictionary();
        this.ocg.setItem(COSName.TYPE, COSName.OCG);
        setName(name);
    }

    /**
     * Creates a new instance based on a given {@link COSDictionary}.
     * @param dict the dictionary
     */
    public PDOptionalContentGroup(COSDictionary dict)
    {
        if (!dict.getItem(COSName.TYPE).equals(COSName.OCG))
        {
            throw new IllegalArgumentException(
                    "Provided dictionary is not of type '" + COSName.OCG + "'");
        }
        this.ocg = dict;
    }

    /** {@inheritDoc} */
    public COSBase getCOSObject()
    {
        return this.ocg;
    }

    /**
     * Returns the name of the optional content group.
     * @return the name
     */
    public String getName()
    {
        return this.ocg.getString(COSName.NAME);
    }

    /**
     * Sets the name of the optional content group.
     * @param name the name
     */
    public void setName(String name)
    {
        this.ocg.setString(COSName.NAME, name);
    }

    //TODO Add support for "Intent" and "Usage"

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return super.toString() + " (" + getName() + ")";
    }

}

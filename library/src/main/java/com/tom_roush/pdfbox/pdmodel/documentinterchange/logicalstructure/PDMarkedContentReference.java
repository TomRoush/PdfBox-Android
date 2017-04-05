package com.tom_roush.pdfbox.pdmodel.documentinterchange.logicalstructure;

import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.common.COSObjectable;

/**
 * A marked-content reference.
 * 
 * @author Ben Litchfield
 */
public class PDMarkedContentReference implements COSObjectable
{
    public static final String TYPE = "MCR";

    private final COSDictionary dictionary;

    /**
     * {@inheritDoc}
     */
    @Override
    public COSDictionary getCOSObject()
    {
        return this.dictionary;
    }

    /**
     * Default constructor
     */
    public PDMarkedContentReference()
    {
        this.dictionary = new COSDictionary();
        this.dictionary.setName(COSName.TYPE, TYPE);
    }

    /**
     * Constructor for an existing marked content reference.
     * 
     * @param dictionary the page dictionary
     */
    public PDMarkedContentReference(COSDictionary dictionary)
    {
        this.dictionary = dictionary;
    }

    /**
     * Gets the page.
     * 
     * @return the page
     */
    public PDPage getPage()
    {
        COSDictionary pg = (COSDictionary) this.getCOSObject().getDictionaryObject(COSName.PG);
        if (pg != null)
        {
            return new PDPage(pg);
        }
        return null;
    }

    /**
     * Sets the page.
     * 
     * @param page the page
     */
    public void setPage(PDPage page)
    {
        this.getCOSObject().setItem(COSName.PG, page);
    }

    /**
     * Gets the marked content identifier.
     * 
     * @return the marked content identifier
     */
    public int getMCID()
    {
        return this.getCOSObject().getInt(COSName.MCID);
    }

    /**
     * Sets the marked content identifier.
     * 
     * @param mcid the marked content identifier
     */
    public void setMCID(int mcid)
    {
        this.getCOSObject().setInt(COSName.MCID, mcid);
    }


    @Override
    public String toString()
    {
        return new StringBuilder().append("mcid=").append(this.getMCID()).toString();
    }
}

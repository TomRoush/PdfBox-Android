package org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

/**
 * A marked-content reference.
 * 
 * @author Ben Litchfield
 */
public class PDMarkedContentReference implements COSObjectable
{

    public static final String TYPE = "MCR";

    private COSDictionary dictionary;

    protected COSDictionary getCOSDictionary()
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
     * {@inheritDoc}
     */
    @Override
    public COSBase getCOSObject()
    {
        return this.dictionary;
    }

    /**
     * Gets the page.
     * 
     * @return the page
     */
    public PDPage getPage()
    {
        COSDictionary pg = (COSDictionary) this.getCOSDictionary()
            .getDictionaryObject(COSName.PG);
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
        this.getCOSDictionary().setItem(COSName.PG, page);
    }

    /**
     * Gets the marked content identifier.
     * 
     * @return the marked content identifier
     */
    public int getMCID()
    {
        return this.getCOSDictionary().getInt(COSName.MCID);
    }

    /**
     * Sets the marked content identifier.
     * 
     * @param mcid the marked content identifier
     */
    public void setMCID(int mcid)
    {
        this.getCOSDictionary().setInt(COSName.MCID, mcid);
    }


    @Override
    public String toString()
    {
        return new StringBuilder()
            .append("mcid=").append(this.getMCID()).toString();
    }

}

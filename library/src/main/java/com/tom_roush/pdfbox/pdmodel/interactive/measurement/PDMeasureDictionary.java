package org.apache.pdfbox.pdmodel.interactive.measurement;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

/**
 * This class represents a measure dictionary.
 */
public class PDMeasureDictionary implements COSObjectable
{
    /**
     * The type of the dictionary.
     */
    public static final String TYPE = "Measure";

    private COSDictionary measureDictionary;

    /**
     * Constructor.
     */
    protected PDMeasureDictionary()
    {
        this.measureDictionary = new COSDictionary();
        this.getDictionary().setName(COSName.TYPE, TYPE);
    }

    /**
     * Constructor.
     * 
     * @param dictionary the corresponding dictionary
     */
    public PDMeasureDictionary(COSDictionary dictionary)
    {
        this.measureDictionary = dictionary;
    }

    /**
     * {@inheritDoc}
     */
    public COSBase getCOSObject()
    {
        return this.measureDictionary;
    }

    /**
     * This will return the corresponding dictionary.
     * 
     * @return the measure dictionary
     */
    public COSDictionary getDictionary()
    {
        return this.measureDictionary;
    }

    /**
     * This will return the type of the measure dictionary.
     * It must be "Measure"
     * 
     * @return the type
     */
    public String getType()
    {
        return TYPE;
    }

    /**
     * returns the subtype of the measure dictionary.
     * @return the subtype of the measure data dictionary
     */

    public String getSubtype()
    {
        return this.getDictionary().getNameAsString(COSName.SUBTYPE,
                PDRectlinearMeasureDictionary.SUBTYPE);
    }

    /**
     * This will set the subtype of the measure dictionary.
     * @param subtype the subtype of the measure dictionary
     */
    protected void setSubtype(String subtype)
    {
        this.getDictionary().setName(COSName.SUBTYPE, subtype);
    }

}

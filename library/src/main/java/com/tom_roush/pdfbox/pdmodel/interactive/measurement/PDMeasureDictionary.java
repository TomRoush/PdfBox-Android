package com.tom_roush.pdfbox.pdmodel.interactive.measurement;

import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.pdmodel.common.COSObjectable;

/**
 * This class represents a measure dictionary.
 */
public class PDMeasureDictionary implements COSObjectable
{
    /**
     * The type of the dictionary.
     */
    public static final String TYPE = "Measure";

    private final COSDictionary measureDictionary;

    /**
     * Constructor.
     */
    protected PDMeasureDictionary()
    {
        this.measureDictionary = new COSDictionary();
        this.getCOSObject().setName(COSName.TYPE, TYPE);
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
     * This will return the corresponding dictionary.
     * 
     * @return the measure dictionary
     */
    @Override
    public COSDictionary getCOSObject()
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
        return this.getCOSObject().getNameAsString(COSName.SUBTYPE,
                PDRectlinearMeasureDictionary.SUBTYPE);
    }

    /**
     * This will set the subtype of the measure dictionary.
     * @param subtype the subtype of the measure dictionary
     */
    protected void setSubtype(String subtype)
    {
        this.getCOSObject().setName(COSName.SUBTYPE, subtype);
    }

}

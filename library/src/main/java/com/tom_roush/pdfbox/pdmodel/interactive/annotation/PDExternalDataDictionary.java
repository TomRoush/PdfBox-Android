package com.tom_roush.pdfbox.pdmodel.interactive.annotation;

import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.pdmodel.common.COSObjectable;

/**
 * This class represents an external data dictionary. 
 */
public class PDExternalDataDictionary implements COSObjectable
{

    private final COSDictionary dataDictionary;

    /**
     * Constructor.
     */
    public PDExternalDataDictionary()
    {
        this.dataDictionary = new COSDictionary();
        this.dataDictionary.setName(COSName.TYPE, "ExData");
    }

    /**
     * Constructor.
     * 
     *  @param dictionary Dictionary
     */
    public PDExternalDataDictionary(COSDictionary dictionary)
    {
        this.dataDictionary = dictionary;
    }

    /**
     * returns the dictionary.
     *
     * @return the dictionary
     */
    @Override
    public COSDictionary getCOSObject()
    {
        return this.dataDictionary;
    }

    /**
     * returns the type of the external data dictionary.
     * It must be "ExData", if present
     * @return the type of the external data dictionary
     */
    public String getType()
    {
        return this.getCOSObject().getNameAsString(COSName.TYPE, "ExData");
    }

    /**
     * returns the subtype of the external data dictionary.
     * @return the subtype of the external data dictionary
     */
    public String getSubtype()
    {
        return this.getCOSObject().getNameAsString(COSName.SUBTYPE);
    }

    /**
     * This will set the subtype of the external data dictionary.
     * @param subtype the subtype of the external data dictionary
     */
    public void setSubtype(String subtype)
    {
        this.getCOSObject().setName(COSName.SUBTYPE, subtype);
    }

}

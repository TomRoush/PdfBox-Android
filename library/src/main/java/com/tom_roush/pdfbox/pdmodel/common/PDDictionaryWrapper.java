package com.tom_roush.pdfbox.pdmodel.common;

import com.tom_roush.pdfbox.cos.COSDictionary;

/**
 * A wrapper for a COS dictionary.
 *
 * @author Ben Litchfield
 *
 */
public class PDDictionaryWrapper implements COSObjectable
{

    private final COSDictionary dictionary;

    /**
     * Default constructor
     */
    public PDDictionaryWrapper()
    {
        this.dictionary = new COSDictionary();
    }

    /**
     * Creates a new instance with a given COS dictionary.
     * 
     * @param dictionary the dictionary
     */
    public PDDictionaryWrapper(COSDictionary dictionary)
    {
        this.dictionary = dictionary;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public COSDictionary getCOSObject()
    {
        return this.dictionary;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj instanceof PDDictionaryWrapper)
        {
            return this.dictionary.equals(((PDDictionaryWrapper) obj).dictionary);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return this.dictionary.hashCode();
    }

}

package com.tom_roush.pdfbox.pdmodel.interactive.action;

import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.pdmodel.common.COSObjectable;

/**
 * This is the implementation of an URI dictionary.
 */
public class PDURIDictionary implements COSObjectable
{

    private final COSDictionary uriDictionary;

    /**
     * Constructor.
     * 
     */
    public PDURIDictionary()
    {
        this.uriDictionary = new COSDictionary();
    }

    /**
     * Constructor.
     * 
     * @param dictionary the corresponding dictionary
     */
    public PDURIDictionary(COSDictionary dictionary)
    {
        this.uriDictionary = dictionary;
    }

    /**
     * Returns the corresponding dictionary.
     * @return the dictionary
     */
    @Override
    public COSDictionary getCOSObject()
    {
        return this.uriDictionary;
    }

    /**
     * This will get the base URI to be used in resolving relative URI references.
     * URI actions within the document may specify URIs in partial form, to be interpreted
     * relative to this base address. If no base URI is specified, such partial URIs
     * will be interpreted relative to the location of the document itself.
     * The use of this entry is parallel to that of the body element &lt;BASE&gt;, as described
     * in the HTML 4.01 Specification.
     *
     * @return The URI entry of the specific URI dictionary.
     */
    public String getBase()
    {
        return this.getCOSObject().getString("Base");
    }

    /**
     * This will set the base URI to be used in resolving relative URI references.
     * URI actions within the document may specify URIs in partial form, to be interpreted
     * relative to this base address. If no base URI is specified, such partial URIs
     * will be interpreted relative to the location of the document itself.
     * The use of this entry is parallel to that of the body element &lt;BASE&gt;, as described
     * in the HTML 4.01 Specification.
     *
     * @param base The the base URI to be used.
     */
    public void setBase(String base)
    {
        this.getCOSObject().setString("Base", base);
    }

}

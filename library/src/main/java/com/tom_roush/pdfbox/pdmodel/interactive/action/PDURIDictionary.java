package org.apache.pdfbox.pdmodel.interactive.action;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

/**
 * This is the implementation of an URI dictionary.
 */
public class PDURIDictionary implements COSObjectable
{

    private COSDictionary uriDictionary;

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
     * {@inheritDoc}
     */
    public COSBase getCOSObject()
    {
        return this.uriDictionary;
    }

    /**
     * Returns the corresponding dictionary.
     * @return the dictionary
     */
    public COSDictionary getDictionary()
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
        return this.getDictionary().getString("Base");
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
        this.getDictionary().setString("Base", base);
    }

}

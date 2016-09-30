package org.apache.pdfbox.pdmodel.interactive.digitalsignature;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;

/**
 * If exist, it describe where the signature handler can request a RFC3161
 * timestamp and if it is a must have for the signature.
 *
 * @author Thomas Chojecki
 */
public class PDSeedValueTimeStamp
{
    private COSDictionary dictionary;

    /**
     * Default constructor.
     */
    public PDSeedValueTimeStamp()
    {
        dictionary = new COSDictionary();
        dictionary.setDirect(true);
    }

    /**
     * Constructor.
     *
     * @param dict The signature dictionary.
     */
    public PDSeedValueTimeStamp(COSDictionary dict)
    {
        dictionary = dict;
        dictionary.setDirect(true);
    }


    /**
     * Convert this standard java object to a COS object.
     *
     * @return The cos object that matches this Java object.
     */
    public COSBase getCOSObject()
    {
        return getDictionary();
    }

    /**
     * Convert this standard java object to a COS dictionary.
     *
     * @return The COS dictionary that matches this Java object.
     */
    public COSDictionary getDictionary()
    {
        return dictionary;
    }

    /**
     * Returns the URL.
     * 
     * @return the URL
     */
    public String getURL()
    {
        return dictionary.getString(COSName.URL);
    }

    /**
     * Sets the URL.
     * @param url the URL to be set as URL
     */
    public void setURL(String url)
    {
        dictionary.setString(COSName.URL, url);
    }

    /**
     * Indicates if a timestamp is required.
     * 
     * @return true if a timestamp is required
     */
    public boolean isTimestampRequired()
    {
        return dictionary.getInt(COSName.FT, 0) != 0;
    }

    /**
     * Sets if a timestamp is reuqired or not.
     * 
     * @param flag true if a timestamp is required
     */
    public void setTimestampRequired(boolean flag)
    {
        dictionary.setInt(COSName.FT, flag ? 1 : 0);
    }
}

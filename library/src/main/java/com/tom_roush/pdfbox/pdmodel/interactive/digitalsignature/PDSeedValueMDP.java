package org.apache.pdfbox.pdmodel.interactive.digitalsignature;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;

/**
 * This MDP dictionary is a part of the seed value dictionary and define
 * if a author signature or a certification signature should be use.
 *
 * @author Thomas Chojecki
 */
public class PDSeedValueMDP
{
    private COSDictionary dictionary;

    /**
     * Default constructor.
     */
    public PDSeedValueMDP()
    {
        dictionary = new COSDictionary();
        dictionary.setDirect(true);
    }

    /**
     * Constructor.
     *
     * @param dict The signature dictionary.
     */
    public PDSeedValueMDP(COSDictionary dict)
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
     * Return the P value.
     * 
     * @return the P value
     */
    public int getP()
    {
        return dictionary.getInt(COSName.P);
    }

    /**
     * Set the P value.
     * 
     * @param p the value to be set as P
     */
    public void setP(int p)
    {
        if (p < 0 || p > 3)
        {
            throw new IllegalArgumentException("Only values between 0 and 3 nare allowed.");
        }
        dictionary.setInt(COSName.P, p);
    }
}

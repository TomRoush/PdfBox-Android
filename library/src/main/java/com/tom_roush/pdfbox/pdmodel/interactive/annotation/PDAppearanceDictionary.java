package org.apache.pdfbox.pdmodel.interactive.annotation;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

/**
 * An appearance dictionary specifying how the annotation shall be presented visually on the page.
 *
 * @author Ben Litchfield
 */
public class PDAppearanceDictionary implements COSObjectable
{
    private final COSDictionary dictionary;

    /**
     * Constructor for embedding.
     */
    public PDAppearanceDictionary()
    {
        dictionary = new COSDictionary();
        //the N entry is required.
        dictionary.setItem( COSName.N, new COSDictionary() );
    }

    /**
     * Constructor for reading.
     *
     * @param dictionary The annotations dictionary.
     */
    public PDAppearanceDictionary( COSDictionary dictionary )
    {
        this.dictionary = dictionary;
    }

    @Override
    public COSDictionary getCOSObject()
    {
        return dictionary;
    }

    /**
     * This will return a list of appearances.  In the case where there is
     * only one appearance the map will contain one entry whose key is the string
     * "default".
     *
     * @return A list of key(java.lang.String) value(PDAppearanceStream) pairs
     */
    public PDAppearanceEntry getNormalAppearance()
    {
        COSBase entry = dictionary.getDictionaryObject( COSName.N );
        if ( entry == null )
        { 
            return null; 
        }
        else
        {
        	return new PDAppearanceEntry(entry);
        }
    }

    /**
     * This will set a list of appearances.  If you would like to set the single
     * appearance then you should use the key "default", and when the PDF is written
     * back to the filesystem then there will only be one stream.
     *
     * @param entry appearance stream or subdictionary
     */
    public void setNormalAppearance( PDAppearanceEntry entry )
    {
        dictionary.setItem( COSName.N, entry );
    }

    /**
     * This will set the normal appearance when there is only one appearance
     * to be shown.
     *
     * @param ap The appearance stream to show.
     */
    public void setNormalAppearance( PDAppearanceStream ap )
    {
        dictionary.setItem( COSName.N, ap );
    }

    /**
     * This will return a list of appearances.  In the case where there is
     * only one appearance the map will contain one entry whose key is the string
     * "default".  If there is no rollover appearance then the normal appearance
     * will be returned.  Which means that this method will never return null.
     *
     * @return A list of key(java.lang.String) value(PDAppearanceStream) pairs
     */
    public PDAppearanceEntry getRolloverAppearance()
    {
        COSBase entry = dictionary.getDictionaryObject( COSName.R );
        if( entry == null )
        {
            return getNormalAppearance();
        }
        else
        {
        	return new PDAppearanceEntry(entry);
        }
    }

    /**
     * This will set a list of appearances.  If you would like to set the single
     * appearance then you should use the key "default", and when the PDF is written
     * back to the filesystem then there will only be one stream.
     *
     * @param entry appearance stream or subdictionary
     */
    public void setRolloverAppearance( PDAppearanceEntry entry )
    {
        dictionary.setItem( COSName.R, entry );
    }

    /**
     * This will set the rollover appearance when there is rollover appearance
     * to be shown.
     *
     * @param ap The appearance stream to show.
     */
    public void setRolloverAppearance( PDAppearanceStream ap )
    {
        dictionary.setItem( COSName.R, ap );
    }

    /**
     * This will return a list of appearances.  In the case where there is
     * only one appearance the map will contain one entry whose key is the string
     * "default".  If there is no rollover appearance then the normal appearance
     * will be returned.  Which means that this method will never return null.
     *
     * @return A list of key(java.lang.String) value(PDAppearanceStream) pairs
     */
    public PDAppearanceEntry getDownAppearance()
    {
        COSBase entry = dictionary.getDictionaryObject( COSName.D );
        if( entry == null )
        {
            return getNormalAppearance();
        }
        else
        {
        	return new PDAppearanceEntry(entry);
        }
    }

    /**
     * This will set a list of appearances.  If you would like to set the single
     * appearance then you should use the key "default", and when the PDF is written
     * back to the filesystem then there will only be one stream.
     *
     * @param entry appearance stream or subdictionary
     */
    public void setDownAppearance( PDAppearanceEntry entry )
    {
        dictionary.setItem( COSName.D, entry );
    }
    
    /**
     * This will set the down appearance when there is down appearance
     * to be shown.
     *
     * @param ap The appearance stream to show.
     */
    public void setDownAppearance( PDAppearanceStream ap )
    {
        dictionary.setItem( COSName.D, ap );
    }
}

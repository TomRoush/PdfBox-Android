package org.apache.pdfboxandroid.pdmodel.interactive.annotation;

import java.util.HashMap;
import java.util.Map;

import org.apache.pdfboxandroid.PDFBox;
import org.apache.pdfboxandroid.cos.COSBase;
import org.apache.pdfboxandroid.cos.COSDictionary;
import org.apache.pdfboxandroid.cos.COSName;
import org.apache.pdfboxandroid.cos.COSStream;
import org.apache.pdfboxandroid.pdmodel.common.COSDictionaryMap;
import org.apache.pdfboxandroid.pdmodel.common.COSObjectable;

import android.util.Log;

/**
 * This class represents a PDF /AP entry the appearance dictionary.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.4 $
 */
public class PDAppearanceDictionary implements COSObjectable {
	private COSDictionary dictionary;
	
	/**
     * Constructor.
     */
    public PDAppearanceDictionary()
    {
        dictionary = new COSDictionary();
        //the N entry is required.
        dictionary.setItem( COSName.N, new COSDictionary() );
    }
	
	/**
     * Constructor.
     *
     * @param dict The annotations dictionary.
     */
    public PDAppearanceDictionary( COSDictionary dict )
    {
        dictionary = dict;
    }
	
	/**
     * returns the dictionary.
     * @return the dictionary
     */
    public COSBase getCOSObject()
    {
        return dictionary;
    }
    
    /**
     * returns the dictionary.
     * @return the dictionary
     */
    public COSDictionary getDictionary()
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
    public Map<String,PDAppearanceStream> getNormalAppearance()
    {
        COSBase ap = dictionary.getDictionaryObject( COSName.N );
        if ( ap == null )
        { 
            return null; 
        }
        else if( ap instanceof COSStream )
        {
            COSStream aux = (COSStream) ap;
            ap = new COSDictionary();
            ((COSDictionary)ap).setItem(COSName.DEFAULT, aux );
        }
        COSDictionary map = (COSDictionary)ap;
        Map<String, PDAppearanceStream> actuals = new HashMap<String, PDAppearanceStream>();
        Map<String, PDAppearanceStream> retval = new COSDictionaryMap<String, PDAppearanceStream>( actuals, map );
        for( COSName asName : map.keySet() )
        {
            COSBase stream = map.getDictionaryObject( asName );
            // PDFBOX-1599: this is just a workaround. The given PDF provides "null" as stream 
            // which leads to a COSName("null") value and finally to a ClassCastExcpetion
            if (stream instanceof COSStream)
            {
                COSStream as = (COSStream)stream;
                actuals.put( asName.getName(), new PDAppearanceStream( as ) );
            }
            else
            {
                Log.d("non-conformance workaround: ignore null value for appearance stream.", PDFBox.LOG_TAG);
            }
        }
        return retval;
    }
    
    /**
     * This will set the normal appearance when there is only one appearance
     * to be shown.
     *
     * @param ap The appearance stream to show.
     */
    public void setNormalAppearance( PDAppearanceStream ap )
    {
        dictionary.setItem( COSName.N, ap.getStream() );
    }
}

package com.tom_roush.pdfbox.pdmodel.interactive.form;

import com.tom_roush.pdfbox.cos.COSArray;
import com.tom_roush.pdfbox.cos.COSBase;
import com.tom_roush.pdfbox.cos.COSString;
import com.tom_roush.pdfbox.pdmodel.common.COSArrayList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A set of utility methods to help with common AcroForm form and field related functions.
 */
public final class FieldUtils
{
    
    /**
     * An implementation of a basic key value pair.
     * 
     * This implementation is used to help sorting the content of
     * field option entries with an array of two-element arrays as
     * used by choice fields.
     * 
     */
    static class KeyValue
    {
        private final String key;
        private final String value;

        KeyValue(final String theKey, final String theValue)
        {
            this.key = theKey;
            this.value = theValue;
        }
        
        public String getKey()
        {
            return this.key;
        }


        public String getValue()
        {
            return this.value;
        }
        
        @Override
        public String toString()
        {
            return "(" + this.key + ", " + this.value + ")";
        }
    }
    
    /**
     * Comparator to sort KeyValue by key.
     */
    static class KeyValueKeyComparator implements Serializable, Comparator<KeyValue>
    {
    	private static final long serialVersionUID = 6715364290007167694L;
    	
        @Override
        public int compare(KeyValue o1, KeyValue o2)
        {
            return o1.key.compareTo(o2.key);
        }
    }

    /**
     * Comparator to sort KeyValue by value.
     */
    static class KeyValueValueComparator implements Serializable, Comparator<KeyValue>
    {
    	private static final long serialVersionUID = -3984095679894798265L;
    	
        @Override
        public int compare(KeyValue o1, KeyValue o2)
        {
            return o1.value.compareTo(o2.value);
        }
    }

    /**
     * Constructor.
     */
    private FieldUtils()
    {
    }
    
    /**
     * Return two related lists as a single list with key value pairs.
     * 
     * @param key the key elements
     * @param value the value elements
     * @return a sorted list of KeyValue elements.
     */
    static List<KeyValue> toKeyValueList(List<String> key, List<String> value)
    {
        List<KeyValue> list = new ArrayList<KeyValue>();
        for(int i =0; i<key.size(); i++)
        {
            list.add(new FieldUtils.KeyValue(key.get(i),value.get(i)));
        }
        return list;
    }    
    
    /**
     * Sort two related lists simultaneously by the elements in the key parameter.
     * 
     * @param pairs a list of KeyValue elements
     */
    static void sortByValue(List<KeyValue> pairs)
    {
        Collections.sort(pairs, new FieldUtils.KeyValueValueComparator());
    }

    /**
     * Sort two related lists simultaneously by the elements in the value parameter.
     * 
     * @param pairs a list of KeyValue elements
     */
    static void sortByKey(List<KeyValue> pairs)
    {
        Collections.sort(pairs, new FieldUtils.KeyValueKeyComparator());
    }
    
    /**
     * Return either one of a list which can have two-element arrays entries.
     * <p>
     * Some entries in a dictionary can either be an array of elements
     * or an array of two-element arrays. This method will either return
     * the elements in the array or in case of two-element arrays, the element
     * designated by the pair index
     * </p>
     * <p>
     * An {@link IllegalArgumentException} will be thrown if the items contain
     * two-element arrays and the index is not 0 or 1.
     * </p>
     *
     * @param items the array of elements or two-element arrays
     * @param pairIdx the index into the two-element array
     * @return a List of single elements
     */
    static List<String> getPairableItems(COSBase items, int pairIdx)
    {
    	if (pairIdx < 0 || pairIdx > 1)
    	{
    		throw new IllegalArgumentException("Only 0 and 1 are allowed as an index into two-element arrays");
    	}
    	
    	if (items instanceof COSString)
    	{
    		List<String> array = new ArrayList<String>();
    		array.add(((COSString) items).getString());
    		return array;
    	}
    	else if (items instanceof COSArray)
    	{
    		// test if there is a single text or a two-element array
    		COSBase entry = ((COSArray) items).get(0);
    		if (entry instanceof COSString)
    		{
    			return COSArrayList.convertCOSStringCOSArrayToList((COSArray)items);
    		}
    		else
    		{
    			return getItemsFromPair(items, pairIdx);
    		}
    	}
    	return Collections.emptyList();
    }
    
    /**
     * Return either one of a list of two-element arrays entries.
     *
     * @param items the array of elements or two-element arrays
     * @param pairIdx the index into the two-element array
     * @return a List of single elements
     */
    private static List<String> getItemsFromPair(COSBase items, int pairIdx)
    {
    	List<String> exportValues = new ArrayList<String>();
    	int numItems = ((COSArray) items).size();
    	for (int i=0;i<numItems;i++)
    	{
    		COSArray pair = (COSArray) ((COSArray) items).get(i);
    		COSString displayValue = (COSString) pair.get(pairIdx);
    		exportValues.add(displayValue.getString());
    	}
    	return exportValues;
    }
}

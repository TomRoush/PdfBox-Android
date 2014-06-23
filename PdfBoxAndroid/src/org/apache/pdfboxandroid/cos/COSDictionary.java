package org.apache.pdfboxandroid.cos;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.pdfboxandroid.exceptions.COSVisitorException;
import org.apache.pdfboxandroid.pdmodel.common.COSObjectable;

public class COSDictionary extends COSBase {
	
	
	/**
	 * The name-value pairs of this dictionary. The pairs are kept in the
	 * order they were added to the dictionary.
	 */
	protected final Map<COSName, COSBase> items =
			new LinkedHashMap<COSName, COSBase>();
	
	/**
	 * Constructor.
	 */
	public COSDictionary()
	{
		//default constructor
	}
	
	/**
	 * Copy Constructor.  This will make a shallow copy of this dictionary.
	 *
	 * @param dict The dictionary to copy.
	 */
	public COSDictionary( COSDictionary dict )
	{
		items.putAll( dict.items );
	}
	
	/**
	 * This is a convenience method that will get the dictionary object that
	 * is expected to be an integer.  If the dictionary value is null then the
	 * default Value will be returned.
	 *
	 * @param key The key to the item in the dictionary.
	 * @param defaultValue The value to return if the dictionary item is null.
	 * @return The integer value.
	 */
	public long getLong( COSName key, long defaultValue )
	{
		long retval = defaultValue;
		COSBase obj = getDictionaryObject( key );
		if( obj != null && obj instanceof COSNumber)
		{
			retval = ((COSNumber)obj).longValue();
		}
		return retval;
	}
	
	/**
	 * This will get an object from this dictionary.  If the object is a reference then it will
	 * dereference it and get it from the document.  If the object is COSNull then
	 * null will be returned.
	 *
	 * @param key The key to the object that we are getting.
	 *
	 * @return The object that matches the key.
	 */
	public COSBase getDictionaryObject( COSName key )
	{
		COSBase retval = items.get( key );
		if( retval instanceof COSObject )
		{
			retval = ((COSObject)retval).getObject();
		}
		if( retval instanceof COSNull )
		{
			retval = null;
		}
		return retval;
	}
	
	/**
	 * This will add all of the dictionarys keys/values to this dictionary.
	 * Only called when adding keys to a trailer that already exists.
	 *
	 * @param dic The dic to get the keys from.
	 */
	public void addAll( COSDictionary dic )
	{
		for( Map.Entry<COSName, COSBase> entry : dic.entrySet() )
		{
			/*
			 * If we're at a second trailer, we have a linearized
			 * pdf file, meaning that the first Size entry represents
			 * all of the objects so we don't need to grab the second.
			 */
			if(!entry.getKey().getName().equals("Size")
					|| !items.containsKey(COSName.getPDFName("Size")))
			{
				setItem( entry.getKey(), entry.getValue() );
			}
		}
	}
	
	/**
	 * Returns the name-value entries in this dictionary. The returned
	 * set is in the order the entries were added to the dictionary.
	 *
	 * @since Apache PDFBox 1.1.0
	 * @return name-value entries in this dictionary
	 */
	public Set<Map.Entry<COSName, COSBase>> entrySet()
	{
		return items.entrySet();
	}
	
	/**
	 * This will set an item in the dictionary.  If value is null then the result
	 * will be the same as removeItem( key ).
	 *
	 * @param key The key to the dictionary object.
	 * @param value The value to the dictionary object.
	 */
	public void setItem( COSName key, COSBase value )
	{
		if( value == null )
		{
			removeItem( key );
		}
		else
		{
			items.put( key, value );
		}
	}
	
	/**
	 * This will remove an item for the dictionary.  This
	 * will do nothing of the object does not exist.
	 *
	 * @param key The key to the item to remove from the dictionary.
	 */
	public void removeItem( COSName key )
	{
		items.remove( key );
	}
	
	/**
	 * This will do a lookup into the dictionary.
	 *
	 * @param key The key to the object.
	 *
	 * @return The item that matches the key.
	 */
	public COSBase getItem( COSName key )
	{
		return items.get( key );
	}
	
	/**
	 * This is a convenience method that will get the dictionary object that
	 * is expected to be a name and convert it to a string.  Null is returned
	 * if the entry does not exist in the dictionary.
	 *
	 * @param key The key to the item in the dictionary.
	 * @return The name converted to a string.
	 */
	public String getString( String key )
	{
		return getString( COSName.getPDFName( key ) );
	}
	
	/**
	 * This is a convenience method that will get the dictionary object that
	 * is expected to be a name and convert it to a string.  Null is returned
	 * if the entry does not exist in the dictionary.
	 *
	 * @param key The key to the item in the dictionary.
	 * @return The name converted to a string.
	 */
	public String getString( COSName key )
	{
		String retval = null;
		COSBase value = getDictionaryObject( key );
		if( value != null && value instanceof COSString)
		{
			retval = ((COSString)value).getString();
		}
		return retval;
	}
	
	/**
	 * Returns the names of the entries in this dictionary. The returned
	 * set is in the order the entries were added to the dictionary.
	 *
	 * @since Apache PDFBox 1.1.0
	 * @return names of the entries in this dictionary
	 */
	public Set<COSName> keySet()
	{
		return items.keySet();
	}
	
	/**
	 * This will get an object from this dictionary.  If the object is a reference then it will
	 * dereference it and get it from the document.  If the object is COSNull then
	 * null will be returned.
	 *
	 * @param key The key to the object that we are getting.
	 *
	 * @return The object that matches the key.
	 */
	public COSBase getDictionaryObject( String key )
	{
		return getDictionaryObject( COSName.getPDFName( key ) );
	}
	
	/**
	 * This is a convenience method that will convert the value to a COSName
	 * object.  If it is null then the object will be removed.
	 *
	 * @param key The key to the object,
	 * @param value The string value for the name.
	 */
	public void setName( COSName key, String value )
	{
		COSName name = null;
		if( value != null )
		{
			name = COSName.getPDFName( value );
		}
		setItem( key, name );
	}
	
	/**
	 * This is a convenience method that will get the dictionary object that
	 * is expected to be a name and convert it to a string.  Null is returned
	 * if the entry does not exist in the dictionary.
	 *
	 * @param key The key to the item in the dictionary.
	 * @return The name converted to a string.
	 */
	public String getNameAsString( COSName key )
	{
		String retval = null;
		COSBase name = getDictionaryObject( key );
		if( name != null )
		{
			if ( name instanceof COSName)
			{
				retval = ((COSName)name).getName();
			}
			else if ( name instanceof COSString)
			{
				retval = ((COSString)name).getString();
			}
		}
		return retval;
	}
	
	/**
	 * This is a special case of getDictionaryObject that takes multiple keys, it will handle
	 * the situation where multiple keys could get the same value, ie if either CS or ColorSpace
	 * is used to get the colorspace.
	 * This will get an object from this dictionary.  If the object is a reference then it will
	 * dereference it and get it from the document.  If the object is COSNull then
	 * null will be returned.
	 *
	 * @param firstKey The first key to try.
	 * @param secondKey The second key to try.
	 *
	 * @return The object that matches the key.
	 */
	public COSBase getDictionaryObject( COSName firstKey, COSName secondKey )
	{
		COSBase retval = getDictionaryObject( firstKey );
		if( retval == null && secondKey != null)
		{
			retval = getDictionaryObject( secondKey );
		}
		return retval;
	}
	
	/**
	 * This is a convenience method that will get the dictionary object that
	 * is expected to be an int.  -1 is returned if there is no value.
	 *
	 * @param key The key to the item in the dictionary.
	 * @return The integer value..
	 */
	public int getInt( COSName key )
	{
		return getInt( key, -1 );
	}
	
	/**
	 * This is a convenience method that will get the dictionary object that
	 * is expected to be an integer.  If the dictionary value is null then the
	 * default Value will be returned.
	 *
	 * @param key The key to the item in the dictionary.
	 * @param defaultValue The value to return if the dictionary item is null.
	 * @return The integer value.
	 */
	public int getInt( COSName key, int defaultValue )
	{
		return getInt( key, null, defaultValue);
	}
	
	/**
	 * This is a convenience method that will get the dictionary object that
	 * is expected to be an integer.  If the dictionary value is null then the
	 * default Value will be returned.
	 *
	 * @param firstKey The first key to the item in the dictionary.
	 * @param secondKey The second key to the item in the dictionary.
	 * @param defaultValue The value to return if the dictionary item is null.
	 * @return The integer value.
	 */
	public int getInt( COSName firstKey, COSName secondKey, int defaultValue )
	{
		int retval = defaultValue;
		COSBase obj = getDictionaryObject( firstKey, secondKey );
		if( obj != null && obj instanceof COSNumber)
		{
			retval = ((COSNumber)obj).intValue();
		}
		return retval;
	}
	
	/**
	 * This is a convenience method that will get the dictionary object that
	 * is expected to be a name and convert it to a string.  Null is returned
	 * if the entry does not exist in the dictionary.
	 *
	 * @param key The key to the item in the dictionary.
	 * @return The name converted to a string.
	 */
	public String getNameAsString( String key )
	{
		return getNameAsString( COSName.getPDFName( key ) );
	}
	
	/**
	 * This will clear all items in the map.
	 */
	public void clear()
	{
		items.clear();
	}
	
	/**
	 * This will return the number of elements in this dictionary.
	 *
	 * @return The number of elements in the dictionary.
	 */
	public int size()
	{
		return items.size();
	}
	
	/**
	 * This is a convenience method that will convert the value to a COSInteger
	 * object.
	 *
	 * @param key The key to the object,
	 * @param value The int value for the name.
	 */
	public void setInt( COSName key, int value )
	{
		setItem( key, COSInteger.get(value) );
	}
	
	/**
	 * This is a convenience method that will convert the value to a COSFloat
	 * object.
	 *
	 * @param key The key to the object,
	 * @param value The int value for the name.
	 */
	public void setFloat( COSName key, float value )
	{
		COSFloat fltVal = new COSFloat( value );
		setItem( key, fltVal );
	}
	
	/**
	 * This is a convenience method that will convert the value to a COSName
	 * object.  If it is null then the object will be removed.
	 *
	 * @param key The key to the object,
	 * @param value The string value for the name.
	 */
	public void setName( String key, String value )
	{
		setName( COSName.getPDFName( key ), value );
	}
	
	/**
	 * This is a convenience method that will convert the value to a COSString
	 * object.  If it is null then the object will be removed.
	 *
	 * @param key The key to the object,
	 * @param value The string value for the name.
	 */
	public void setString( COSName key, String value )
	{
		COSString name = null;
		if( value != null )
		{
			name = new COSString( value );
		}
		setItem( key, name );
	}
	
	/**
	 * This will set an item in the dictionary.  If value is null then the result
	 * will be the same as removeItem( key ).
	 *
	 * @param key The key to the dictionary object.
	 * @param value The value to the dictionary object.
	 */
	public void setItem( String key, COSObjectable value )
	{
		setItem( COSName.getPDFName( key ), value );
	}
	
	/**
	 * This will set an item in the dictionary.  If value is null then the result
	 * will be the same as removeItem( key ).
	 *
	 * @param key The key to the dictionary object.
	 * @param value The value to the dictionary object.
	 */
	public void setItem( COSName key, COSObjectable value )
	{
		COSBase base = null;
		if( value != null )
		{
			base = value.getCOSObject();
		}
		setItem( key, base );
	}
	
	/**
	 * This is a convenience method that will get the dictionary object that
	 * is expected to be an float.  If the dictionary value is null then the
	 * default Value will be returned.
	 *
	 * @param key The key to the item in the dictionary.
	 * @param defaultValue The value to return if the dictionary item is null.
	 * @return The float value.
	 */
	public float getFloat( COSName key, float defaultValue )
	{
		float retval = defaultValue;
		COSBase obj = getDictionaryObject( key );
		if( obj != null && obj instanceof COSNumber)
		{
			retval = ((COSNumber)obj).floatValue();
		}
		return retval;
	}
	
	/**
	 * This will get all of the values for the dictionary.
	 *
	 * @return All the values for the dictionary.
	 */
	public Collection<COSBase> getValues()
	{
		return items.values();
	}
	
	/**
	 * This is a convenience method that will get the dictionary object that
	 * is expected to be an long.  -1 is returned if there is no value.
	 *
	 * @param key The key to the item in the dictionary.
	 * @return The long value.
	 */
	public long getLong( COSName key )
	{
		return getLong( key, -1L );
	}

	/**
	 * visitor pattern double dispatch method.
	 *
	 * @param visitor The object to notify when visiting this object.
	 * @return The object that the visitor returns.
	 *
	 * @throws COSVisitorException If there is an error visiting this object.
	 */
	public Object accept(ICOSVisitor  visitor) throws COSVisitorException
	{
		return visitor.visitFromDictionary(this);
	}
	
	/**
	 * This is a convenience method that will convert the value to a COSInteger
	 * object.
	 *
	 * @param key The key to the object,
	 * @param value The int value for the name.
	 */
	public void setLong( COSName key, long value )
	{
		COSInteger intVal = null;
		intVal = COSInteger.get(value);
		setItem( key, intVal );
	}
}

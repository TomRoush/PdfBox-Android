package org.apache.pdfboxandroid.cos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.pdfboxandroid.exceptions.COSVisitorException;

/**
 * An array of PDFBase objects as part of the PDF document.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.24 $
 */
public class COSArray extends COSBase implements Iterable<COSBase> {
	private List<COSBase> objects = new ArrayList<COSBase>();

	/**
     * Get access to the list.
     *
     * @return an iterator over the array elements
     */
    public Iterator<COSBase> iterator()
    {
        return objects.iterator();
    }
    
    /**
     * This will get the size of this array.
     *
     * @return The number of elements in the array.
     */
    public int size()
    {
        return objects.size();
    }
    
    /**
     * This will get an object from the array.  This will NOT derefernce
     * the COS object.
     *
     * @param index The index into the array to get the object.
     *
     * @return The object at the requested index.
     */
    public COSBase get( int index )
    {
        return objects.get( index );
    }
    
    /**
     * This will remove an element from the array.
     *
     * @param i The index of the object to remove.
     *
     * @return The object that was removed.
     */
    public COSBase remove( int i )
    {
        return objects.remove( i );
    }
    
    /**
     * This will add an object to the array.
     *
     * @param object The object to add to the array.
     */
    public void add( COSBase object )
    {
        objects.add( object );
    }
    
    /**
     * Get the value of the array as an integer.
     *
     * @param index The index into the list.
     *
     * @return The value at that index or -1 if it is null.
     */
    public int getInt( int index )
    {
        return getInt( index, -1 );
    }
    
    /**
     * Get the value of the array as an integer, return the default if it does
     * not exist.
     *
     * @param index The value of the array.
     * @param defaultValue The value to return if the value is null.
     * @return The value at the index or the defaultValue.
     */
    public int getInt( int index, int defaultValue )
    {
        int retval = defaultValue;
        if ( index < size() )
        {
            Object obj = objects.get( index );
            if( obj instanceof COSNumber )
            {
                retval = ((COSNumber)obj).intValue();
            }
        }
        return retval;
    }
    
    /**
     * This will get an object from the array.  This will dereference the object.
     * If the object is COSNull then null will be returned.
     *
     * @param index The index into the array to get the object.
     *
     * @return The object at the requested index.
     */
    public COSBase getObject( int index )
    {
        Object obj = objects.get( index );
        if( obj instanceof COSObject )
        {
            obj = ((COSObject)obj).getObject();
        }
        else if( obj instanceof COSNull )
        {
            obj = null;
        }
        return (COSBase)obj;
    }
    
    /**
     * This will add an object to the array.
     *
     * @param objectsList The object to add to the array.
     */
    public void addAll( Collection<COSBase> objectsList )
    {
        objects.addAll( objectsList );
    }
    
    /**
     * Add the specified object at the ith location and push the rest to the
     * right.
     *
     * @param i The index to add at.
     * @param objectList The object to add at that index.
     */
    public void addAll( int i, Collection<COSBase> objectList )
    {
        objects.addAll( i, objectList );
    }
    
    /**
     * This will remove all of the objects in the collection.
     *
     * @param objectsList The list of objects to remove from the collection.
     */
    public void removeAll( Collection<COSBase> objectsList )
    {
        objects.removeAll( objectsList );
    }
    
    /**
     * This will retain all of the objects in the collection.
     *
     * @param objectsList The list of objects to retain from the collection.
     */
    public void retainAll( Collection<COSBase> objectsList )
    {
        objects.retainAll( objectsList );
    }
    
    /**
     * This will remove all of the objects in the collection.
     */
    public void clear()
    {
        objects.clear();
    }
    
    /**
     * This will set an object at a specific index.
     *
     * @param index zero based index into array.
     * @param object The object to set.
     */
    public void set( int index, COSBase object )
    {
        objects.set( index, object );
    }
    
    /**
     * Add the specified object at the ith location and push the rest to the
     * right.
     *
     * @param i The index to add at.
     * @param object The object to add at that index.
     */
    public void add( int i, COSBase object)
    {
        objects.add( i, object );
    }
    
    /**
     * This will take an COSArray of numbers and convert it to a float[].
     *
     * @return This COSArray as an array of float numbers.
     */
    public float[] toFloatArray()
    {
        float[] retval = new float[size()];
        for( int i=0; i<size(); i++ )
        {
            retval[i] = ((COSNumber)getObject( i )).floatValue();
        }
        return retval;
    }

    /**
     * visitor pattern double dispatch method.
     *
     * @param visitor The object to notify when visiting this object.
     * @return any object, depending on the visitor implementation, or null
     * @throws COSVisitorException If an error occurs while visiting this object.
     */
    @Override
    public Object accept(ICOSVisitor visitor) throws COSVisitorException
    {
        return visitor.visitFromArray(this);
    }
}

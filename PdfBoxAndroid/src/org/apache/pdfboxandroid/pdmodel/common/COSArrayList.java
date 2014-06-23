package org.apache.pdfboxandroid.pdmodel.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.pdfboxandroid.cos.COSArray;
import org.apache.pdfboxandroid.cos.COSBase;
import org.apache.pdfboxandroid.cos.COSDictionary;
import org.apache.pdfboxandroid.cos.COSFloat;
import org.apache.pdfboxandroid.cos.COSInteger;
import org.apache.pdfboxandroid.cos.COSName;
import org.apache.pdfboxandroid.cos.COSNull;
import org.apache.pdfboxandroid.cos.COSNumber;
import org.apache.pdfboxandroid.cos.COSString;

/**
 * This is an implementation of a List that will sync its contents to a COSArray.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.15 $
 */
public class COSArrayList<E> implements List<E> {
	private COSArray array;
    private List<E> actual;
    
    private COSDictionary parentDict;
    private COSName dictKey;
	
	/**
     * Constructor.
     *
     * @param actualList The list of standard java objects
     * @param cosArray The COS array object to sync to.
     */
    public COSArrayList( List<E> actualList, COSArray cosArray )
    {
        actual = actualList;
        array = cosArray;
    }
    
    /**
     * {@inheritDoc}
     */
    public int size()
    {
        return actual.size();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEmpty()
    {
        return actual.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(Object o)
    {
        return actual.contains(o);
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<E> iterator()
    {
        return actual.iterator();
    }

    /**
     * {@inheritDoc}
     */
    public Object[] toArray()
    {
        return actual.toArray();
    }

    /**
     * {@inheritDoc}
     */
    public <X>X[] toArray(X[] a)
    {
        return actual.toArray(a);

    }

    /**
     * {@inheritDoc}
     */
    public boolean add(E o)
    {
        //when adding if there is a parentDict then change the item
        //in the dictionary from a single item to an array.
        if( parentDict != null )
        {
            parentDict.setItem( dictKey, array );
            //clear the parent dict so it doesn't happen again, there might be
            //a usecase for keeping the parentDict around but not now.
            parentDict = null;
        }
        //string is a special case because we can't subclass to be COSObjectable
        if( o instanceof String )
        {
            array.add( new COSString( (String)o ) );
        }
        else if( o instanceof DualCOSObjectable )
        {
            DualCOSObjectable dual = (DualCOSObjectable)o;
            array.add( dual.getFirstCOSObject() );
            array.add( dual.getSecondCOSObject() );
        }
        else
        {
            if(array != null)
            {
                array.add(((COSObjectable)o).getCOSObject());
            }
        }
        return actual.add(o);
    }

    /**
     * {@inheritDoc}
     */
    public boolean remove(Object o)
    {
        boolean retval = true;
        int index = actual.indexOf( o );
        if( index >= 0 )
        {
            actual.remove( index );
            array.remove( index );
        }
        else
        {
            retval = false;
        }
        return retval;
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsAll(Collection<?> c)
    {
        return actual.containsAll( c );
    }

    /**
     * {@inheritDoc}
     */
    public boolean addAll(Collection<? extends E> c)
    {
        //when adding if there is a parentDict then change the item
        //in the dictionary from a single item to an array.
        if( parentDict != null && c.size() > 0)
        {
            parentDict.setItem( dictKey, array );
            //clear the parent dict so it doesn't happen again, there might be
            //a usecase for keeping the parentDict around but not now.
            parentDict = null;
        }
        array.addAll( toCOSObjectList( c ) );
        return actual.addAll( c );
    }

    /**
     * {@inheritDoc}
     */
    public boolean addAll(int index, Collection<? extends E> c)
    {
        //when adding if there is a parentDict then change the item
        //in the dictionary from a single item to an array.
        if( parentDict != null && c.size() > 0)
        {
            parentDict.setItem( dictKey, array );
            //clear the parent dict so it doesn't happen again, there might be
            //a usecase for keeping the parentDict around but not now.
            parentDict = null;
        }

        if( c.size() >0 && c.toArray()[0] instanceof DualCOSObjectable )
        {
            array.addAll( index*2, toCOSObjectList( c ) );
        }
        else
        {
            array.addAll( index, toCOSObjectList( c ) );
        }
        return actual.addAll( index, c );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean removeAll(Collection<?> c)
    {
        array.removeAll( toCOSObjectList( c ) );
        return actual.removeAll( c );
    }

    /**
     * {@inheritDoc}
     */
    public boolean retainAll(Collection<?> c)
    {
        array.retainAll( toCOSObjectList( c ) );
        return actual.retainAll( c );
    }

    /**
     * {@inheritDoc}
     */
    public void clear()
    {
        //when adding if there is a parentDict then change the item
        //in the dictionary from a single item to an array.
        if( parentDict != null )
        {
            parentDict.setItem( dictKey, (COSBase)null );
        }
        actual.clear();
        array.clear();
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object o)
    {
        return actual.equals( o );
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode()
    {
        return actual.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    public E get(int index)
    {
        return actual.get( index );

    }

    /**
     * {@inheritDoc}
     */
    public E set(int index, E element)
    {
        if( element instanceof String )
        {
            COSString item = new COSString( (String)element );
            if( parentDict != null && index == 0 )
            {
                parentDict.setItem( dictKey, item );
            }
            array.set( index, item );
        }
        else if( element instanceof DualCOSObjectable )
        {
            DualCOSObjectable dual = (DualCOSObjectable)element;
            array.set( index*2, dual.getFirstCOSObject() );
            array.set( index*2+1, dual.getSecondCOSObject() );
        }
        else
        {
            if( parentDict != null && index == 0 )
            {
                parentDict.setItem( dictKey, ((COSObjectable)element).getCOSObject() );
            }
            array.set( index, ((COSObjectable)element).getCOSObject() );
        }
        return actual.set( index, element );
    }

    /**
     * {@inheritDoc}
     */
    public void add(int index, E element)
    {
        //when adding if there is a parentDict then change the item
        //in the dictionary from a single item to an array.
        if( parentDict != null )
        {
            parentDict.setItem( dictKey, array );
            //clear the parent dict so it doesn't happen again, there might be
            //a usecase for keeping the parentDict around but not now.
            parentDict = null;
        }
        actual.add( index, element );
        if( element instanceof String )
        {
            array.add( index, new COSString( (String)element ) );
        }
        else if( element instanceof DualCOSObjectable )
        {
            DualCOSObjectable dual = (DualCOSObjectable)element;
            array.add( index*2, dual.getFirstCOSObject() );
            array.add( index*2+1, dual.getSecondCOSObject() );
        }
        else
        {
            array.add( index, ((COSObjectable)element).getCOSObject() );
        }
    }

    /**
     * {@inheritDoc}
     */
    public E remove(int index)
    {
        if( array.size() > index && array.get( index ) instanceof DualCOSObjectable )
        {
            //remove both objects
            array.remove( index );
            array.remove( index );
        }
        else
        {
            array.remove( index );
        }
        return actual.remove( index );
    }

    /**
     * {@inheritDoc}
     */
    public int indexOf(Object o)
    {
        return actual.indexOf( o );
    }

    /**
     * {@inheritDoc}
     */
    public int lastIndexOf(Object o)
    {
        return actual.indexOf( o );

    }

    /**
     * {@inheritDoc}
     */
    public ListIterator<E> listIterator()
    {
        return actual.listIterator();
    }

    /**
     * {@inheritDoc}
     */
    public ListIterator<E> listIterator(int index)
    {
        return actual.listIterator( index );
    }

    /**
     * {@inheritDoc}
     */
    public List<E> subList(int fromIndex, int toIndex)
    {
        return actual.subList( fromIndex, toIndex );
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return "COSArrayList{" + array.toString() + "}";
    }
    
    /**
     * This will return then underlying COSArray.
     * 
     * @return the COSArray
     */
    public COSArray toList() 
    {
        return array;
    }
    
    private List<COSBase> toCOSObjectList( Collection<?> list )
    {
        List<COSBase> cosObjects = new ArrayList<COSBase>();
        Iterator<?> iter = list.iterator();
        while( iter.hasNext() )
        {
            Object next = iter.next();
            if( next instanceof String )
            {
                cosObjects.add( new COSString( (String)next ) );
            }
            else if( next instanceof DualCOSObjectable )
            {
                DualCOSObjectable object = (DualCOSObjectable)next;
                array.add( object.getFirstCOSObject() );
                array.add( object.getSecondCOSObject() );
            }
            else
            {
                COSObjectable cos = (COSObjectable)next;
                cosObjects.add( cos.getCOSObject() );
            }
        }
        return cosObjects;
    }
    
    /**
     * This will convert a list of COSObjectables to an
     * array list of COSBase objects.
     *
     * @param cosObjectableList A list of COSObjectable.
     *
     * @return A list of COSBase.
     */
    public static COSArray converterToCOSArray( List<?> cosObjectableList )
    {
        COSArray array = null;
        if( cosObjectableList != null )
        {
            if( cosObjectableList instanceof COSArrayList )
            {
                //if it is already a COSArrayList then we don't want to recreate the array, we want to reuse it.
                array = ((COSArrayList<?>)cosObjectableList).array;
            }
            else
            {
                array = new COSArray();
                Iterator<?> iter = cosObjectableList.iterator();
                while( iter.hasNext() )
                {
                    Object next = iter.next();
                    if( next instanceof String )
                    {
                        array.add( new COSString( (String)next ) );
                    }
                    else if( next instanceof Integer || next instanceof Long )
                    {
                        array.add( COSInteger.get( ((Number)next).longValue() ) );
                    }
                    else if( next instanceof Float || next instanceof Double )
                    {
                        array.add( new COSFloat( ((Number)next).floatValue() ) );
                    }
                    else if( next instanceof COSObjectable )
                    {
                        COSObjectable object = (COSObjectable)next;
                        array.add( object.getCOSObject() );
                    }
                    else if( next instanceof DualCOSObjectable )
                    {
                        DualCOSObjectable object = (DualCOSObjectable)next;
                        array.add( object.getFirstCOSObject() );
                        array.add( object.getSecondCOSObject() );
                    }
                    else if( next == null )
                    {
                        array.add( COSNull.NULL );
                    }
                    else
                    {
                        throw new RuntimeException( "Error: Don't know how to convert type to COSBase '" +
                        next.getClass().getName() + "'" );
                    }
                }
            }
        }
        return array;
    }
    
    /**
     * This will take an array of COSNumbers and return a COSArrayList of
     * java.lang.Float values.
     *
     * @param floatArray The existing float Array.
     *
     * @return The list of Float objects.
     */
    public static List<Float> convertFloatCOSArrayToList( COSArray floatArray )
    {
        List<Float> retval = null;
        if( floatArray != null )
        {
            List<Float> numbers = new ArrayList<Float>();
            for( int i=0; i<floatArray.size(); i++ )
            {
                numbers.add( new Float( ((COSNumber)floatArray.get( i )).floatValue() ) );
            }
            retval = new COSArrayList<Float>( numbers, floatArray );
        }
        return retval;
    }
}

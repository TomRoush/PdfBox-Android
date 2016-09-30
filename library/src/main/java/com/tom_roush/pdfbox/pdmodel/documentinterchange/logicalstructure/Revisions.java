package com.tom_roush.pdfbox.pdmodel.documentinterchange.logicalstructure;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Johannes Koch
 *
 * @param <T> the type of object to store the revision numbers with
 */
public class Revisions<T>
{
    private List<T> objects;
    private List<Integer> revisionNumbers;

    private List<T> getObjects()
    {
        if (this.objects == null)
        {
            this.objects = new ArrayList<T>();
        }
        return this.objects;
    }

    private List<Integer> getRevisionNumbers()
    {
        if (this.revisionNumbers == null)
        {
            this.revisionNumbers = new ArrayList<Integer>();
        }
        return this.revisionNumbers;
    }

    /**
     * 
     */
    public Revisions()
    {
    }

    /**
     * Returns the object at the specified position.
     * 
     * @param index the position
     * @return the object
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public T getObject(int index)
    {
        return this.getObjects().get(index);
    }

    /**
     * Returns the revision number at the specified position.
     * 
     * @param index the position
     * @return the revision number
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public int getRevisionNumber(int index)
    {
        return this.getRevisionNumbers().get(index);
    }

    /**
     * Adds an object with a specified revision number.
     * 
     * @param object the object
     * @param revisionNumber the revision number
     */
    public void addObject(T object, int revisionNumber)
    {
        this.getObjects().add(object);
        this.getRevisionNumbers().add(revisionNumber);
    }

    /**
     * Sets the revision number of a specified object.
     * 
     * @param object the object
     * @param revisionNumber the revision number
     */
    protected void setRevisionNumber(T object, int revisionNumber)
    {
        int index = this.getObjects().indexOf(object);
        if (index > -1)
        {
            this.getRevisionNumbers().set(index, revisionNumber);
        }
    }

    /**
     * Returns the size.
     * 
     * @return the size
     */
    public int size()
    {
        return this.getObjects().size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.getObjects().size(); i++)
        {
            if (i > 0)
            {
                sb.append("; ");
            }
            sb.append("object=").append(this.getObjects().get(i))
                .append(", revisionNumber=").append(this.getRevisionNumber(i));
        }
        return sb.toString();
    }
}

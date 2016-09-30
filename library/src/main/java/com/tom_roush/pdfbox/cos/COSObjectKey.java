package org.apache.pdfbox.cos;


/**
 * Object representing the physical reference to an indirect pdf object.
 *
 * @author Michael Traut
 */
public class COSObjectKey implements Comparable<COSObjectKey>
{
    private final long number;
    private final int generation;

    /**
     * PDFObjectKey constructor comment.
     *
     * @param object The object that this key will represent.
     */
    public COSObjectKey(COSObject object)
    {
    	this(object.getObjectNumber(), object.getGenerationNumber());
    }

    /**
     * PDFObjectKey constructor comment.
     *
     * @param num The object number.
     * @param gen The object generation number.
     */
    public COSObjectKey(long num, int gen)
    {
        number = num;
        generation = gen;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj)
    {
    	COSObjectKey objToBeCompared = obj instanceof COSObjectKey ? (COSObjectKey)obj : null;
    	return objToBeCompared != null &&
    			objToBeCompared.getNumber() == getNumber() &&
    			objToBeCompared.getGeneration() == getGeneration();
    }

    /**
     * This will get the generation number.
     *
     * @return The objects generation number.
     */
    public int getGeneration()
    {
        return generation;
    }
    /**
     * This will get the objects id.
     *
     * @return The object's id.
     */
    public long getNumber()
    {
        return number;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
    	return Long.valueOf(number+generation).hashCode();
    }

    @Override
    public String toString()
    {
    	return Long.toString(number) + " " + Integer.toString(generation) + " R";
    }

    @Override
    public int compareTo(COSObjectKey other)
    {
        if (getNumber() < other.getNumber())
        {
            return -1;
        }
        else if (getNumber() > other.getNumber())
        {
            return 1;
        }
        else
        {
            if (getGeneration() < other.getGeneration())
            {
                return -1;
            }
            else if (getGeneration() > other.getGeneration())
            {
                return 1;
            }
            else
            {
                return 0;
            }
        }
    }
}